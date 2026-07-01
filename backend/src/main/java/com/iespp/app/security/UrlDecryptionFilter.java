package com.iespp.app.security;

import com.iespp.app.utils.CifradoUrlUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UrlDecryptionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        String path = httpReq.getRequestURI();
        String cleanPath = path;
        if (cleanPath.startsWith("/")) cleanPath = cleanPath.substring(1);

        if (CifradoUrlUtil.esEncriptado(cleanPath)) {
            System.out.println("[UrlDecryptionFilter] Encrypted path detected: " + cleanPath);
            String decrypted = CifradoUrlUtil.desencriptar(cleanPath);
            System.out.println("[UrlDecryptionFilter] Decrypted to: " + decrypted);
            decrypted = decrypted.toLowerCase(Locale.ROOT);
            System.out.println("[UrlDecryptionFilter] Lowercased to: " + decrypted);
            if (!decrypted.isEmpty()) {
                String finalPath = decrypted;
                String finalQuery = null;

                int qIdx = decrypted.indexOf('?');
                if (qIdx >= 0) {
                    finalPath = decrypted.substring(0, qIdx);
                    finalQuery = decrypted.substring(qIdx + 1);
                }

                if (!finalPath.startsWith("/")) finalPath = "/" + finalPath;

                final String resolvedPath = finalPath;
                final String resolvedQuery = finalQuery;

                HttpServletRequestWrapper wrapped = new HttpServletRequestWrapper(httpReq) {
                    @Override
                    public String getRequestURI() {
                        return resolvedPath;
                    }

                    @Override
                    public String getServletPath() {
                        String p = resolvedPath;
                        int idx = p.indexOf(';');
                        return idx >= 0 ? p.substring(0, idx) : p;
                    }

                    @Override
                    public String getPathInfo() {
                        return null;
                    }

                    @Override
                    public String getQueryString() {
                        return resolvedQuery;
                    }

                    @Override
                    public String getParameter(String name) {
                        if (resolvedQuery == null) return null;
                        String[] vals = getParameterMap().get(name);
                        return vals != null && vals.length > 0 ? vals[0] : null;
                    }

                    @Override
                    public Map<String, String[]> getParameterMap() {
                        if (resolvedQuery == null) return Collections.emptyMap();
                        Map<String, String[]> map = new LinkedHashMap<>();
                        for (String pair : resolvedQuery.split("&")) {
                            int eq = pair.indexOf('=');
                            if (eq >= 0) {
                                String key = pair.substring(0, eq);
                                String val = pair.substring(eq + 1);
                                map.compute(key, (k, v) -> {
                                    if (v == null) return new String[]{val};
                                    String[] arr = Arrays.copyOf(v, v.length + 1);
                                    arr[v.length] = val;
                                    return arr;
                                });
                            }
                        }
                        return map;
                    }

                    @Override
                    public Enumeration<String> getParameterNames() {
                        return Collections.enumeration(getParameterMap().keySet());
                    }

                    @Override
                    public String[] getParameterValues(String name) {
                        return getParameterMap().get(name);
                    }
                };
                chain.doFilter(wrapped, response);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
