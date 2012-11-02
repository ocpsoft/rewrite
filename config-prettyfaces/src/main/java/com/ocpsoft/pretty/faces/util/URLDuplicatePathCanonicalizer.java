/*
 * Copyright 2010 Lincoln Baxter, III
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ocpsoft.pretty.faces.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class URLDuplicatePathCanonicalizer
{
    private final Pattern pattern = Pattern.compile("(/[^/]+/)\\.\\.(/[^/]+/)");

    /**
     * Canonicalize a given URL, replacing relative path structures with the
     * condensed path. (Eg. /xxxx/../xxxx/ => /xxxx/)
     * 
     * @return Return the canonicalized URL. If the URL requires no
     *         canonicalization, return the original unmodified URL.
     */
    public String canonicalize(final String url)
    {
        if (url != null && url.contains("/../"))
        {
            StringBuffer result = new StringBuffer();
            Matcher m = pattern.matcher(url);
            while (m.find())
            {
                if (m.group(1).equals(m.group(2)))
                {
                    m.appendReplacement(result, m.group(1));
                }
                m.appendTail(result);
            }
            return result.toString();
        }
        return url;
    }

}
