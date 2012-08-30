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


import com.google.inject.Singleton;

import com.nesscomputing.httpclient.HttpClientObserver;
import com.nesscomputing.httpclient.HttpClientRequest;
import com.nesscomputing.httpclient.HttpClientRequest.Builder;

@Singleton
public class HttpClientServerInfoObserver extends HttpClientObserver
{
    public static final String X_NESS_SERVER_BINARY = "X-Ness-Server-Binary";
    public static final String X_NESS_SERVER_VERSION = "X-Ness-Server-Version";
    public static final String X_NESS_SERVER_MODE = "X-Ness-Server-Mode";
    public static final String X_NESS_SERVER_TYPE = "X-Ness-Server-Type";
    public static final String X_NESS_SERVER_TOKEN = "X-Ness-Server-Token";

    @Override
    public <RequestType> HttpClientRequest<RequestType> onRequestSubmitted(final HttpClientRequest<RequestType> request)
    {
        final Builder<RequestType> builder = HttpClientRequest.Builder.fromRequest(request);

        addHeader(builder, ServerInfo.SERVER_BINARY, X_NESS_SERVER_BINARY);
        addHeader(builder, ServerInfo.SERVER_VERSION, X_NESS_SERVER_VERSION);
        addHeader(builder, ServerInfo.SERVER_MODE, X_NESS_SERVER_MODE);
        addHeader(builder, ServerInfo.SERVER_TYPE, X_NESS_SERVER_TYPE);
        addHeader(builder, ServerInfo.SERVER_TOKEN, X_NESS_SERVER_TOKEN);

        return builder.request();
    }

    private <RequestType> void addHeader(final Builder<RequestType> builder, final String serverInfoKey, final String headerName)
    {
        final Object value = ServerInfo.get(serverInfoKey);
        if (value != null) {
            builder.replaceHeader(headerName, value.toString());
        }
    }
}
