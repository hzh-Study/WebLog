package com.quanxiaoha.weblog.common.utils;

import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;


/**
 * @author yjf
 * @description 访客IP归属地工具类
 */
@Slf4j
public class AgentRegionUtils {

    private static volatile byte[] CACHED_XDB_CONTENT;

    /**
     * ip2region 当前 xdb 为 IPv4 库；环回、空、非法及 IPv6 地址不做查询时的展示文案
     */
    private static final String LOCAL_OR_UNSUPPORTED_REGION = "中国-本地-内网-访问";

    /**
     * 获取http请求ip
     * @param request 请求
     * @return ipAddress
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割 "***.***.***.***".length()
            if (ipAddress != null && ipAddress.length() > 15) {
                // = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(",")).trim();
                }
            }
            // 环回（127.0.0.1、::1、0:0:0:0:0:0:0:1 等）替换为本机网卡地址，便于 IPv4 归属地库查询
            if (ipAddress != null && !ipAddress.isEmpty()) {
                try {
                    if (InetAddress.getByName(ipAddress.trim()).isLoopbackAddress()) {
                        ipAddress = InetAddress.getLocalHost().getHostAddress();
                    }
                } catch (UnknownHostException ignored) {
                    // 保持原 ipAddress
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }

    /**
     * 根据ip 获取归属地
     * @param ip 访问ip
     * @return 返回归属地结果
     */
    public static String getIpRegion(String ip, String xdbPath) {
        if (skipIp2RegionLookup(ip)) {
            return LOCAL_OR_UNSUPPORTED_REGION;
        }
        // 1、创建 searcher 对象
        String country = "中国";
        String hdu = "0";
        Searcher searcher;
        try {
            searcher = createSearcher(xdbPath);
        } catch (IOException e) {
            log.error("failed to create searcher with {}: {}\n", xdbPath, e);
            return "外太空";
        }

        // 2、查询 ip = "175.24.184.183";
        try {
            String region = searcher.search(ip);
            region = region.replace("|", " ");
            String[] cityList = region.split(" ");
            if (cityList.length > 0) {
                // 国内的显示到具体的省
                if (country.equals(cityList[0])) {
                    if (cityList.length > 1) {
                        log.info(cityList[0]+"-"+cityList[2]+"-"+cityList[3]+"-"+cityList[4]);
                        return cityList[0]+"-"+cityList[2]+"-"+cityList[3]+"-"+cityList[4];
                    }
                } else if (hdu.equals(cityList[0])) {
                    return "中国-浙江省-杭州市-HDU";
                } else {
                    // 国外显示到国家城市
                    if (cityList.length > 1) {
                        return cityList[0]+"-"+cityList[2];
                    }
                }

            }
        } catch (Exception e) {
            log.error("failed to search({}): {}\n", ip, e);
            throw new BizException(ResponseCodeEnum.AGENT_REGION_SEARCH_ERROR);
        } finally {
            // 3、关闭资源
            try {
                searcher.close();
            } catch (IOException e) {
                log.error("failed to close searcher:", e);
            }
        }

        // 备注：并发使用，每个线程需要创建一个独立的 searcher 对象单独使用。

        return "未知";
    }

    /**
     * ip2region xdb 为 IPv4；IPv6、环回、无法解析的字符串会导致 search 抛错，此处直接跳过查询。
     */
    private static boolean skipIp2RegionLookup(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return true;
        }
        String s = ip.trim();
        if (s.indexOf(':') >= 0) {
            return true;
        }
        try {
            InetAddress addr = InetAddress.getByName(s);
            return addr.isLoopbackAddress();
        } catch (UnknownHostException e) {
            return true;
        }
    }

    private static Searcher createSearcher(String xdbPath) throws IOException {
        if (xdbPath == null || xdbPath.trim().isEmpty() || "xxx".equalsIgnoreCase(xdbPath.trim())) {
            throw new IOException("xdbPath is empty or placeholder");
        }

        String p = xdbPath.trim();
        if (!p.startsWith("classpath:")) {
            return Searcher.newWithFileOnly(p);
        }

        String resourcePath = p.substring("classpath:".length());
        if (resourcePath.startsWith("/")) {
            resourcePath = resourcePath.substring(1);
        }

        byte[] content = CACHED_XDB_CONTENT;
        if (content == null) {
            synchronized (AgentRegionUtils.class) {
                content = CACHED_XDB_CONTENT;
                if (content == null) {
                    try (InputStream in = AgentRegionUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
                        if (in == null) {
                            throw new IOException("xdb resource not found: " + resourcePath);
                        }
                        content = toByteArray(in);
                        CACHED_XDB_CONTENT = content;
                    }
                }
            }
        }

        return Searcher.newWithBuffer(Objects.requireNonNull(content));
    }

    private static byte[] toByteArray(InputStream in) throws IOException {
        byte[] buffer = new byte[8192];
        int read;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return out.toByteArray();
    }
}
