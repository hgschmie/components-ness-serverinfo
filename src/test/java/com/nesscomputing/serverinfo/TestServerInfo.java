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

import org.apache.log4j.MDC;
import org.junit.Assert;
import org.junit.Test;

import com.nesscomputing.testing.lessio.AllowLocalFileAccess;

@AllowLocalFileAccess(paths="*.jar")
public class TestServerInfo
{
    @Test
    public void testEmpty()
    {
        Assert.assertNull(ServerInfo.get("foo"));
        Assert.assertNull(MDC.get("foo"));
    }

    @Test
    public void testValue()
    {
        ServerInfo.add("hello", "world");
        Assert.assertEquals("world", ServerInfo.get("hello"));
        Assert.assertEquals("world", MDC.get("hello").toString());
    }

    @Test
    public void testValueChange()
    {
        ServerInfo.add("hello", "world");
        Assert.assertEquals("world", ServerInfo.get("hello"));
        Assert.assertEquals("world", MDC.get("hello").toString());

        ServerInfo.add("hello", "moon");
        Assert.assertEquals("moon", ServerInfo.get("hello"));
        Assert.assertEquals("moon", MDC.get("hello").toString());
    }

    @Test
    public void testNullValue()
    {
        ServerInfo.add("hello", null);
        Assert.assertEquals(null, ServerInfo.get("hello"));
        Assert.assertEquals(null, MDC.get("hello"));
    }

    @Test
    public void testProviderValue()
    {
        final DataProvider p = new DataProvider();
        ServerInfo.add("hello", p);

        p.setValue("xxx");

        Assert.assertEquals(p, ServerInfo.get("hello"));
        Assert.assertEquals("xxx", MDC.get("hello").toString());

        p.setValue("yyy");

        Assert.assertEquals(p, ServerInfo.get("hello"));
        Assert.assertEquals("yyy", MDC.get("hello").toString());
    }

    @Test
    public void testClear()
    {
        ServerInfo.add("hello", "world");
        Assert.assertEquals("world", ServerInfo.get("hello"));
        Assert.assertEquals("world", MDC.get("hello").toString());

        ServerInfo.clear();

        Assert.assertNull(ServerInfo.get("hello"));
        Assert.assertNull(MDC.get("hello"));
    }

    @Test
    public void testRemove()
    {
        ServerInfo.add("hello", "world");
        Assert.assertEquals("world", ServerInfo.get("hello"));
        Assert.assertEquals("world", MDC.get("hello").toString());

        ServerInfo.remove("hello");

        Assert.assertNull(ServerInfo.get("hello"));
        Assert.assertNull(MDC.get("hello"));
    }

    @Test(expected=NullPointerException.class)
    public void testNullAdd()
    {
        ServerInfo.add(null, "foo");
    }

    @Test(expected=NullPointerException.class)
    public void testNullRemove()
    {
        ServerInfo.remove(null);
    }

    @Test(expected=NullPointerException.class)
    public void testNullGet()
    {
        ServerInfo.get(null);
    }

    public static class DataProvider
    {
        private String value = "";

        public DataProvider()
        {
        }

        public void setValue(final String value)
        {
            this.value = value;
        }

        @Override
        public String toString()
        {
            return value;
        }
    }
}

