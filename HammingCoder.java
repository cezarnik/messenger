/**
 * Created by user on 31.10.2017.
 */
public class HammingCoder extends Coder {

    //for coding
    byte[] arr;
    byte[] block;


    //for decoding
    byte[] arr_d;
    byte[] block_d;
    byte[] check_arr_d;
    HammingCoder(){
        block = new byte[12];
        arr = new byte[8];

        arr_d = new byte[12];
        block_d = new byte[8];
        check_arr_d = new byte[12];
    }

    void code_block(){
        int len = arr.length;
        int new_len = 12;
        byte res[] = block;

        for(int i=1,e = 0;i<=12;++i){
            if((i&(i-1))!=0){
                res[i-1]  = arr[e];
                e++;
            }
        }
        //control bit count
        for(int i=1;i<=12;++i){
            for(int e=0;e<=3;++e){
                if((i&(1<<e))>0){
                    res[(1<<e)-1]^=res[i-1];
                }
            }
        }
    }
    byte[] add_block(byte[] arr){
        int len = arr.length;

        int rem = len%8;
        rem = 8 - rem;
        byte[] res = new byte[rem+len];
        for(int i=0;i<len;++i){
            res[i] = arr[i];
        }
        res[len] = 1;
        return res;
    }

    byte[] rem_block(byte[] arr){
        int n = arr.length-1;
        while(arr[n]==0)
            n--;
        byte res[] = new byte[n];
        for(int i=0;i<n;++i){
            res[i] = arr[i];
        }
        return res;
    }

    void decode_block() throws Exception{
        byte[] arr = arr_d;
        check(arr);
        int len = arr.length;
        if(len != 12){
            throw new Exception("block size is not 12");
        }
        int new_len = 8;
        byte check_arr[] = check_arr_d;
        for(int e = 1;e<=len;++e){
            if((e&(e-1))!=0){ // not 2^i
                check_arr[e-1] = arr[e-1];
            } else {
                check_arr[e-1] = 0;
            }
        }

        //control bit count
        for(int i=1;i<=12;++i){
            for(int e=0;e<=3;++e){
                if((i&(1<<e))>0){
                    check_arr[(1<<e)-1]^=check_arr[i-1];
                }
            }
        }

        int sum = 0;
        for(int i=1;i<=len;++i){
            if(((i-1)&i)==0){ // pow of 2
                if(check_arr[i-1] != arr[i-1]){
                    sum +=i;
                }
            }
        }
        if(sum!=0){
            sum--;
            arr[sum]^=1;
        }


        byte res[] = block_d;
        for(int i=1,e = 0;i<=len;++i) {
            if ((i & (i - 1)) > 0) {// now power of 2
                res[e] = arr[i - 1];
                e++;
            }
        }
    }


    @Override
    byte[] code(byte[] val)  throws Exception{

        int len = val.length;
        if(len % 8!=0){
            throw new Exception("block size is not 8");
        }
        int new_len = 12 * len/8;
        byte res[] = new byte[new_len];
        for(int i=0;i<len;i=i+8){
            for(int e=0;e<8;++e){
                arr[e] = val[i+e];
            }
            code_block();
            for(int e=0;e<12;++e){
                res[i/8 * 12 + e] = block[e];
            }
        }

        res = add_block(res);
        return res;
    }



    @Override
    byte[] decode(byte[] val) throws Exception{
      //  val = rem_block(val);
        int len = val.length /12 * 12;
        if(len % 12 !=0){
            throw new Exception("block size is not 12");
        }
        int new_len = len/12 * 8;
        byte res[] = new byte[new_len];
        for(int i=0;i<len;i=i+12) {
            for (int e = 0; e < 12; ++e) {
                arr_d[e] = val[i + e];
            }
            decode_block();
            for (int e = 0; e < 8; ++e) {
                res[i / 12 * 8 + e] = block_d[e];
            }
        }
        return res;
    }
}
