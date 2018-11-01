package io.nbs.sdk.constants;

/**
 * Copyright © 2015-2020 NBSChain Holdings Limited.
 * All rights reserved.
 *
 * @project nbs-client4j
 * <p>
 * Author   : lanbery
 * Created  : 2018/10/19
 */
public enum  ConfigKeys {
    nickname("nickname",""),
    avatarHash("avatar",""),
    avatarName("avatar-name",""),
    formid("fromid",""),
    avatarSuffix("suffix",".png");
    private String key;
    private Object defVal;

    private ConfigKeys(String key, Object defVal) {
        this.key = key;
        this.defVal = defVal;
    }

    public String key() {
        return key;
    }

    public Object defaultValue() {
        return defVal;
    }


    @Override
    public String toString() {
        return this.key+":"+this.defVal;
    }

    public String toJson(){
        StringBuffer json = new StringBuffer();
        json.append("{\"").append(this.key).append("\" : ");
        if(this.defVal instanceof Number){
            json.append(this.defVal).append("}");
        }else {
            json .append("\"").append(this.defVal.toString()).append("\"}");
        }
        return json.toString();
    }
}
