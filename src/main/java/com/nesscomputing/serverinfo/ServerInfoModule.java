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

import com.google.inject.AbstractModule;

import com.nesscomputing.httpclient.guice.HttpClientModule;

public class ServerInfoModule extends AbstractModule
{
    @Override
    public void configure()
    {
        // Expose server info through Jersey.
        bind(ServerInfoResource.class);

        // Hook up server info headers to the HttpClient.
        HttpClientModule.bindNewObserver(binder()).to(HttpClientServerInfoObserver.class);
    }
}
