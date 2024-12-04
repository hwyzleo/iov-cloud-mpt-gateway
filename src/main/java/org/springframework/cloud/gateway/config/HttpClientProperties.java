package org.springframework.cloud.gateway.config;

import jakarta.validation.constraints.Max;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.ResourceUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.SslProvider;
import reactor.netty.transport.ProxyProvider;

import javax.net.ssl.KeyManagerFactory;
import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@ConfigurationProperties("spring.cloud.gateway.httpclient")
@Validated
public class HttpClientProperties {
    private Integer connectTimeout;
    private Duration responseTimeout;
    private DataSize maxHeaderSize;
    private DataSize maxInitialLineLength;
    private Pool pool = new Pool();
    private Proxy proxy = new Proxy();
    private Ssl ssl = new Ssl();
    private Websocket websocket = new Websocket();
    private boolean wiretap;
    private boolean compression;

    public HttpClientProperties() {
    }

    public Integer getConnectTimeout() {
        return this.connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Duration getResponseTimeout() {
        return this.responseTimeout;
    }

    public void setResponseTimeout(Duration responseTimeout) {
        this.responseTimeout = responseTimeout;
    }

    public @Max(2147483647L) DataSize getMaxHeaderSize() {
        return this.maxHeaderSize;
    }

    public void setMaxHeaderSize(DataSize maxHeaderSize) {
        this.maxHeaderSize = maxHeaderSize;
    }

    public @Max(2147483647L) DataSize getMaxInitialLineLength() {
        return this.maxInitialLineLength;
    }

    public void setMaxInitialLineLength(DataSize maxInitialLineLength) {
        this.maxInitialLineLength = maxInitialLineLength;
    }

    public Pool getPool() {
        return this.pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public Ssl getSsl() {
        return this.ssl;
    }

    public void setSsl(Ssl ssl) {
        this.ssl = ssl;
    }

    public Websocket getWebsocket() {
        return this.websocket;
    }

    public void setWebsocket(Websocket websocket) {
        this.websocket = websocket;
    }

    public boolean isWiretap() {
        return this.wiretap;
    }

    public void setWiretap(boolean wiretap) {
        this.wiretap = wiretap;
    }

    public boolean isCompression() {
        return this.compression;
    }

    public void setCompression(boolean compression) {
        this.compression = compression;
    }

    public String toString() {
        return (new ToStringCreator(this)).append("connectTimeout", this.connectTimeout).append("responseTimeout", this.responseTimeout).append("maxHeaderSize", this.maxHeaderSize).append("maxInitialLineLength", this.maxInitialLineLength).append("pool", this.pool).append("proxy", this.proxy).append("ssl", this.ssl).append("websocket", this.websocket).append("wiretap", this.wiretap).append("compression", this.compression).toString();
    }

    public static class Pool {
        private PoolType type;
        private String name;
        private Integer maxConnections;
        private Long acquireTimeout;
        private Duration maxIdleTime;
        private Duration maxLifeTime;
        private Duration evictionInterval;
        private boolean metrics;

        public Pool() {
            this.type = HttpClientProperties.Pool.PoolType.ELASTIC;
            this.name = "proxy";
            this.maxConnections = ConnectionProvider.DEFAULT_POOL_MAX_CONNECTIONS;
            this.acquireTimeout = ConnectionProvider.DEFAULT_POOL_ACQUIRE_TIMEOUT;
            this.maxIdleTime = null;
            this.maxLifeTime = null;
            this.evictionInterval = Duration.ZERO;
            this.metrics = false;
        }

        public PoolType getType() {
            return this.type;
        }

        public void setType(PoolType type) {
            this.type = type;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getMaxConnections() {
            return this.maxConnections;
        }

        public void setMaxConnections(Integer maxConnections) {
            this.maxConnections = maxConnections;
        }

        public Long getAcquireTimeout() {
            return this.acquireTimeout;
        }

        public void setAcquireTimeout(Long acquireTimeout) {
            this.acquireTimeout = acquireTimeout;
        }

        public Duration getMaxIdleTime() {
            return this.maxIdleTime;
        }

        public void setMaxIdleTime(Duration maxIdleTime) {
            this.maxIdleTime = maxIdleTime;
        }

        public Duration getMaxLifeTime() {
            return this.maxLifeTime;
        }

        public void setMaxLifeTime(Duration maxLifeTime) {
            this.maxLifeTime = maxLifeTime;
        }

        public Duration getEvictionInterval() {
            return this.evictionInterval;
        }

        public void setEvictionInterval(Duration evictionInterval) {
            this.evictionInterval = evictionInterval;
        }

        public boolean isMetrics() {
            return this.metrics;
        }

        public void setMetrics(boolean metrics) {
            this.metrics = metrics;
        }

        public String toString() {
            return "Pool{type=" + this.type + ", name='" + this.name + "', maxConnections=" + this.maxConnections + ", acquireTimeout=" + this.acquireTimeout + ", maxIdleTime=" + this.maxIdleTime + ", maxLifeTime=" + this.maxLifeTime + ", evictionInterval=" + this.evictionInterval + ", metrics=" + this.metrics + "}";
        }

        public static enum PoolType {
            ELASTIC,
            FIXED,
            DISABLED;

            private PoolType() {
            }
        }
    }

    public static class Proxy {
        private ProxyProvider.Proxy type;
        private String host;
        private Integer port;
        private String username;
        private String password;
        private String nonProxyHostsPattern;

        public Proxy() {
            this.type = reactor.netty.transport.ProxyProvider.Proxy.HTTP;
        }

        public ProxyProvider.Proxy getType() {
            return this.type;
        }

        public void setType(ProxyProvider.Proxy type) {
            this.type = type;
        }

        public String getHost() {
            return this.host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return this.port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getUsername() {
            return this.username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return this.password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getNonProxyHostsPattern() {
            return this.nonProxyHostsPattern;
        }

        public void setNonProxyHostsPattern(String nonProxyHostsPattern) {
            this.nonProxyHostsPattern = nonProxyHostsPattern;
        }

        public String toString() {
            return "Proxy{type='" + this.type + "'host='" + this.host + "', port=" + this.port + ", username='" + this.username + "', password='" + this.password + "', nonProxyHostsPattern='" + this.nonProxyHostsPattern + "'}";
        }
    }

    public static class Ssl {
        private boolean useInsecureTrustManager = false;
        private List<String> trustedX509Certificates = new ArrayList();
        private Duration handshakeTimeout = Duration.ofMillis(10000L);
        private Duration closeNotifyFlushTimeout = Duration.ofMillis(3000L);
        private Duration closeNotifyReadTimeout;
        /** @deprecated */
        @Deprecated
        private SslProvider.DefaultConfigurationType defaultConfigurationType;
        private String keyStore;
        private String keyStoreType;
        private String keyStoreProvider;
        private String keyStorePassword;
        private String keyPassword;

        public Ssl() {
            this.closeNotifyReadTimeout = Duration.ZERO;
            this.defaultConfigurationType = SslProvider.DefaultConfigurationType.TCP;
            this.keyStoreType = "JKS";
        }

        public String getKeyStorePassword() {
            return this.keyStorePassword;
        }

        public void setKeyStorePassword(String keyStorePassword) {
            this.keyStorePassword = keyStorePassword;
        }

        public String getKeyStoreType() {
            return this.keyStoreType;
        }

        public void setKeyStoreType(String keyStoreType) {
            this.keyStoreType = keyStoreType;
        }

        public String getKeyStoreProvider() {
            return this.keyStoreProvider;
        }

        public void setKeyStoreProvider(String keyStoreProvider) {
            this.keyStoreProvider = keyStoreProvider;
        }

        public String getKeyStore() {
            return this.keyStore;
        }

        public void setKeyStore(String keyStore) {
            this.keyStore = keyStore;
        }

        public String getKeyPassword() {
            return this.keyPassword;
        }

        public void setKeyPassword(String keyPassword) {
            this.keyPassword = keyPassword;
        }

        public List<String> getTrustedX509Certificates() {
            return this.trustedX509Certificates;
        }

        public void setTrustedX509Certificates(List<String> trustedX509) {
            this.trustedX509Certificates = trustedX509;
        }

        /** @deprecated */
        @Deprecated
        public X509Certificate[] getTrustedX509CertificatesForTrustManager() {
            try {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                ArrayList<Certificate> allCerts = new ArrayList();
                Iterator var3 = this.getTrustedX509Certificates().iterator();

                while(var3.hasNext()) {
                    String trustedCert = (String)var3.next();

                    try {
                        URL url = ResourceUtils.getURL(trustedCert);
                        Collection<? extends Certificate> certs = certificateFactory.generateCertificates(url.openStream());
                        allCerts.addAll(certs);
                    } catch (IOException var7) {
                        throw new WebServerException("Could not load certificate '" + trustedCert + "'", var7);
                    }
                }

                return (X509Certificate[])allCerts.toArray(new X509Certificate[allCerts.size()]);
            } catch (CertificateException var8) {
                throw new WebServerException("Could not load CertificateFactory X.509", var8);
            }
        }

        /** @deprecated */
//        @Deprecated
//        public KeyManagerFactory getKeyManagerFactory() {
//            try {
//                if (this.getKeyStore() != null && this.getKeyStore().length() > 0) {
//                    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//                    char[] keyPassword = this.getKeyPassword() != null ? this.getKeyPassword().toCharArray() : null;
//                    if (keyPassword == null && this.getKeyStorePassword() != null) {
//                        keyPassword = this.getKeyStorePassword().toCharArray();
//                    }
//
//                    keyManagerFactory.init(this.createKeyStore(), keyPassword);
//                    return keyManagerFactory;
//                } else {
//                    return null;
//                }
//            } catch (Exception var3) {
//                throw new IllegalStateException(var3);
//            }
//        }

        /** @deprecated */
        @Deprecated
        public KeyStore createKeyStore() {
            try {
                KeyStore store = this.getKeyStoreProvider() != null ? KeyStore.getInstance(this.getKeyStoreType(), this.getKeyStoreProvider()) : KeyStore.getInstance(this.getKeyStoreType());

                try {
                    URL url = ResourceUtils.getURL(this.getKeyStore());
                    store.load(url.openStream(), this.getKeyStorePassword() != null ? this.getKeyStorePassword().toCharArray() : null);
                } catch (Exception var3) {
                    throw new WebServerException("Could not load key store ' " + this.getKeyStore() + "'", var3);
                }

                return store;
            } catch (NoSuchProviderException | KeyStoreException var4) {
                throw new WebServerException("Could not load KeyStore for given type and provider", var4);
            }
        }

        public boolean isUseInsecureTrustManager() {
            return this.useInsecureTrustManager;
        }

        public void setUseInsecureTrustManager(boolean useInsecureTrustManager) {
            this.useInsecureTrustManager = useInsecureTrustManager;
        }

        public Duration getHandshakeTimeout() {
            return this.handshakeTimeout;
        }

        public void setHandshakeTimeout(Duration handshakeTimeout) {
            this.handshakeTimeout = handshakeTimeout;
        }

        public Duration getCloseNotifyFlushTimeout() {
            return this.closeNotifyFlushTimeout;
        }

        public void setCloseNotifyFlushTimeout(Duration closeNotifyFlushTimeout) {
            this.closeNotifyFlushTimeout = closeNotifyFlushTimeout;
        }

        public Duration getCloseNotifyReadTimeout() {
            return this.closeNotifyReadTimeout;
        }

        public void setCloseNotifyReadTimeout(Duration closeNotifyReadTimeout) {
            this.closeNotifyReadTimeout = closeNotifyReadTimeout;
        }

        /** @deprecated */
        @Deprecated
        public SslProvider.DefaultConfigurationType getDefaultConfigurationType() {
            return this.defaultConfigurationType;
        }

        /** @deprecated */
        @Deprecated
        public void setDefaultConfigurationType(SslProvider.DefaultConfigurationType defaultConfigurationType) {
            this.defaultConfigurationType = defaultConfigurationType;
        }

        public String toString() {
            return (new ToStringCreator(this)).append("useInsecureTrustManager", this.useInsecureTrustManager).append("trustedX509Certificates", this.trustedX509Certificates).append("handshakeTimeout", this.handshakeTimeout).append("closeNotifyFlushTimeout", this.closeNotifyFlushTimeout).append("closeNotifyReadTimeout", this.closeNotifyReadTimeout).append("defaultConfigurationType", this.defaultConfigurationType).toString();
        }
    }

    public static class Websocket {
        private Integer maxFramePayloadLength;
        private boolean proxyPing = true;

        public Websocket() {
        }

        public Integer getMaxFramePayloadLength() {
            return this.maxFramePayloadLength;
        }

        public void setMaxFramePayloadLength(Integer maxFramePayloadLength) {
            this.maxFramePayloadLength = maxFramePayloadLength;
        }

        public boolean isProxyPing() {
            return this.proxyPing;
        }

        public void setProxyPing(boolean proxyPing) {
            this.proxyPing = proxyPing;
        }

        public String toString() {
            return (new ToStringCreator(this)).append("maxFramePayloadLength", this.maxFramePayloadLength).append("proxyPing", this.proxyPing).toString();
        }
    }
}
