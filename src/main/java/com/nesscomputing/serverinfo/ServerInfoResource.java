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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.collect.ImmutableMap;

@Path("/server-info")
public class ServerInfoResource
{
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServerInfo()
    {
        final ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        builder.put("server-type", ServerInfo.get(ServerInfo.SERVER_TYPE))
               .put("server-token", ServerInfo.get(ServerInfo.SERVER_TOKEN));

        final Object binaryVersion = ServerInfo.get(ServerInfo.SERVER_BINARY);
        if (binaryVersion != null) {
            builder.put("server-binary", binaryVersion)
                   .put("server-version", ServerInfo.get(ServerInfo.SERVER_VERSION))
                   .put("server-mode", ServerInfo.get(ServerInfo.SERVER_MODE));
        }

        return Response.ok(builder.build()).build();
    }
}
