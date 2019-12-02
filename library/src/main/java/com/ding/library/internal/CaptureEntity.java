package com.ding.library.internal;

import java.io.Serializable;

/**
 * author:DingDeGao
 * time:2019-10-31-15:15
 * function: UIEntity
 */
public class CaptureEntity implements Serializable {

    public String requestMethod = "";

    public String requestUrl = "";

    public String requestHeader = "";

    public String requestBody = "";


    public String responseStatus = "";

    public String responseHeader = "";

    public String responseBody = "";

    @Override
    public String toString() {
        return requestMethod
                + "\n\n" + "------------requestUrl------------"+"\n\n"+
                requestUrl
                + "\n\n" + "------------requestHeader---------"+"\n\n"+
                requestHeader
                + "\n\n" + "-------------requestBody---------"+"\n\n"+
                requestBody
                + "\n\n" + "------------responseStatus---------"+"\n\n"+
                responseStatus
                + "\n\n" + "------------responseHeader--------"+"\n\n"+
                responseHeader
                + "\n\n" + "-------------responseBody---------"+"\n\n"+
                responseBody+"\n\n";
    }
}
