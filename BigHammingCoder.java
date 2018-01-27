import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by user on 20.11.2017.
 */
public class BigHammingCoder extends Coder {
    HammingCoder hammingCoder;
    byte[] cur;
    byte[] cur1;
    BigHammingCoder(){
        hammingCoder = new HammingCoder();
        cur = new byte[1024 * 8];
        cur1 = new byte[1537 * 8];
    }

    byte[] code(byte[] arr) throws Exception{

        int len = arr.length;
        int new_len = (len/1024 * 1537);
        byte res[] = new byte[new_len];
        int top = 0;
        for(int i=0;i<len;i=i+1024){
            if(i+1024<=len){
                for(int e=0;e<1024*8;++e){
                   cur[e] = 0;
                }
                for(int e=0;e<1024;++e){
                    for(int bit = 0;bit<8;++bit){
                        if((arr[i+e]&(1<<bit))!=0){
                            cur[e*8+bit] = 1;
                        }
                    }
                }
                byte[] val = hammingCoder.code(cur);
                for(int e=0;e<val.length;e=e+8){
                    byte x = 0;
                    for(int bit =0;bit<8;++bit){
                        if(val[e+bit]>0){
                            x|=(1<<bit);
                        }
                    }
                    res[top++] = x;
                }
            } else{
                byte[] val = new byte[(len%1024)*8];
                for(int e=0;i+e<len;++e){
                    for(int bit = 0;bit<8;++bit){
                        if((arr[i+e]&(1<<bit))!=0){
                            val[e*8 + bit] = 1;
                        }
                    }
                }
                val = hammingCoder.code(val);
                int add_len = val.length/8;
                byte[] res1 = new byte[new_len + add_len];
                for(int e=0;e<res.length;++e){
                    res1[e] = res[e];
                }
                top = res.length;
                res = res1;
                for(int e=0;e<val.length;e=e+8){
                    byte x = 0;
                    for(int bit =0;bit<8;++bit){
                        if(val[e+bit]>0){
                            x|=(1<<bit);
                        }
                    }
                    res[top++] = x;
                }
            }
        }
        return res;
    }

    byte[] decode(byte[] arr) throws Exception{
        int len = arr.length;
        int add = len%1537;

        int new_len = (len/1537)*1024;

        byte[] res = new byte[new_len];
        int top = 0;
        for(int i=0;i<len;i=i+1537){
            if(i+1537<=len){
                Arrays.fill(cur1,(byte)0);
                for(int e=0;e<1537;++e){
                    for(int bit = 0;bit<8;++bit){
                        if((arr[i+e]&(1<<bit))!=0){
                            cur1[e*8+bit] = 1;
                        }
                    }
                }
                byte[] val = hammingCoder.decode(cur1);
                for(int e=0;e<val.length;e=e+8){
                    byte x = 0;
                    for(int bit =0;bit<8;++bit){
                        if(val[e+bit]>0){
                            x|=(1<<bit);
                        }
                    }
                    res[top++] = x;
                }
            } else{
                byte[] val = new byte[add * 8];
                for(int e=0;e<add;++e){
                    for(int bit = 0;bit<8;++bit){
                        if((arr[i+e]&(1<<bit))!=0){
                            val[e*8+bit] = 1;
                        }
                    }
                }
                val = hammingCoder.decode(val);
                int add_len = val.length/8;
                byte[] res1 = new byte[new_len + add_len];
                for(int e=0;e<res.length;++e){
                    res1[e] = res[e];
                }
                top = res.length;
                res = res1;
                for(int e=0;e<val.length;e=e+8){
                    byte x = 0;
                    for(int bit =0;bit<8;++bit){
                        if(val[e+bit]>0){
                            x|=(1<<bit);
                        }
                    }
                    res[top++] = x;
                }
            }
        }
        if(top!=res.length){

        }
        return res;
    }

}
