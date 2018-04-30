/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.crawler.client.smb2;

public class ACE {
    private com.hierynomus.msdtyp.ace.ACE ace;
    
    private int type;
    
    private String accountName;
    
    ACE(com.hierynomus.msdtyp.ace.ACE ace) {
        this.setAce(ace);
    }

    public com.hierynomus.msdtyp.ace.ACE getAce() {
        return ace;
    }

    public void setAce(com.hierynomus.msdtyp.ace.ACE ace) {
        this.ace = ace;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAccountName() {
        return this.accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
