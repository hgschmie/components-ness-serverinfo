/**
 * Copyright (C) 2012 Ness Computing, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nesscomputing.serverinfo;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import org.apache.log4j.MDC;

import com.nesscomputing.logging.Log;


/**
 * ServerInfo contains information about the running server such as the type of binary,
 * the binary version, what service type the service itself exposes etc.
 *
 * This implementation shadow-copies the data into the MDC to expose it to third party places
 * in the service such as logging.
 *
 * This is not a guice managed singleton because it is required before guice has ever run (e.g.
 * in the server startup ) and outside the guice environment (e.g. logging).
 */
public final class ServerInfo
{
    private static final Log LOG = Log.findLog();

    private static final Object NULL_VALUE = new Object();

    private static final ServerInfo SERVER_INFO;

    /** Name of the maven artifact that contains this server. */
    public static final String SERVER_BINARY = "server-binary";

    /** Version of the maven artifact that contains this server. */
    public static final String SERVER_VERSION = "server-version";

    /** Mode in which the server is running. Currently, only 'galaxy' and 'solo' are available. */
    public static final String SERVER_MODE = "server-mode";

    /** Type of the server, as set by the main server class {@link StandaloneServer#getServerType()}. */
    public static final String SERVER_TYPE = "server-type";

    /** Server token, unique identifier set when the server is started {@link StandaloneServer#getServerToken()}. */
    public static final String SERVER_TOKEN = "server-token";

    // Keys in the jar manifest
    private static final String NESS_BINARY_KEY = "X-Ness-Binary";
    private static final String NESS_VERSION_KEY = "X-Ness-Version";
    private static final String NESS_MODE_KEY = "X-Ness-Mode";

    private final Map<String, Object> info = new ConcurrentHashMap<String, Object>();

    static {
        // Try to load the server info based on the initial stack trace. The bottommost
        // class *should* be the main class, but just in case, go up the stack until
        // a class with a main method is found.
        final StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();

        String baseClass = null;
        for (int i = stacktrace.length - 1; i >= 0; i--) {

            // Hack to make unit tests actually work in the crazy
            // classworlds surefire junit jungle on maven...
            if(stacktrace[i].getClassName().contains("surefire")) {
                continue;
            }

            if ("main".equals(stacktrace[i].getMethodName())) {
                baseClass = stacktrace[i].getClassName();
                break;
            }
        }

        SERVER_INFO = new ServerInfo(baseClass);
    }

    public static void add(@Nonnull final String key, @Nullable final Object value)
    {
        SERVER_INFO.register(key, value);
    }

    public static void remove(@Nonnull final String key)
    {
        SERVER_INFO.unregister(key);
    }

    public static Object get(@Nonnull final String key)
    {
        return SERVER_INFO.retrieve(key);
    }

    public static void clear()
    {
        SERVER_INFO.clean();
    }

    public static void registerServerInfo(@Nonnull final String className)
    {
        SERVER_INFO.registerServerInformation(className);
    }

    private ServerInfo(final String baseClass)
    {
        if (baseClass != null) {
            registerServerInformation(baseClass);
        }
    }

    private void register(final String key, final Object value)
    {
        Preconditions.checkNotNull(key != null, "The key must not be null!");

        // If the value put in, remove any fetcher that might exist. This keeps the
        // semantics that a null object will return null when calling MDC.get(key).
        if (value != null) {
            info.put(key, value);
            MDC.put(key, new Object() {
                @Override
                public String toString() {
                    return String.valueOf(get(key));
                }
            });
        }
        else {
            info.put(key, NULL_VALUE);
            MDC.remove(key);
        }
    }

    private void unregister(final String key)
    {
        Preconditions.checkNotNull(key != null, "The key must not be null!");

        MDC.remove(key);
        info.remove(key);
    }

    private Object retrieve(final String key)
    {
        Preconditions.checkNotNull(key != null, "The key must not be null!");

        final Object o = info.get(key);
        return (o == NULL_VALUE) ? null : o;
    }

    private void clean()
    {
        for (Iterator<String> it = info.keySet().iterator(); it.hasNext(); ) {
            final String key = it.next();
            MDC.remove(key);
            it.remove();
        }
    }

    private void registerServerInformation(final String className)
    {
        try {
            final Enumeration<URL> manifests = ServerInfo.class.getClassLoader().getResources("META-INF/MANIFEST.MF");

            while (manifests.hasMoreElements()) {
                try {
                    final URL url = manifests.nextElement();
                    final Manifest manifest = new Manifest(url.openStream());
                    final Attributes classAttributes = manifest.getAttributes(className);
                    if (classAttributes != null) {
                        register(SERVER_BINARY, classAttributes.getValue(NESS_BINARY_KEY));
                        register(SERVER_VERSION, classAttributes.getValue(NESS_VERSION_KEY));
                        register(SERVER_MODE, classAttributes.getValue(NESS_MODE_KEY));
                        LOG.debug("Found manifest, set values to binary=%s, version=%s, type=%s", retrieve(SERVER_BINARY), retrieve(SERVER_VERSION), retrieve(SERVER_MODE));
                        return;
                    }
                }
                catch (IOException ioe) {
                    LOG.warnDebug(ioe, "While loading manifest");
                }
                catch (NoClassDefFoundError ncdfe) {
                    LOG.warnDebug(ncdfe, "While loading manifest!");
                }
            }
            LOG.info("Could not locate manifest information for server, looked for %s.", className);
        }
        catch (IOException ioe) {
            LOG.warnDebug(ioe, "Manifest information is unavailable!");
        }
    }


    @Override
    public String toString()
    {
        return info.toString();
    }
}
