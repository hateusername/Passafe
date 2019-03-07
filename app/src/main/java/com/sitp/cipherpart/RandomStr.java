package com.sitp.cipherpart;

public class RandomStr {
    
    /**
     * 单元测试
     * 运行： java RandomStr 4  (生成长度为4的字符串)
     */
    /*public static void main(String[] args){
        int len = 10;
        System.out.println(randomStr(len,true,true,true,true));
    }*/
    
    /**
     * 返回随机字符串，同时包含数字、大小写字母
     * @param len 字符串长度，不能小于3
     * @return String 随机字符串
     */
    public static String randomStr(int len,boolean hasLowerAlphabet,
			boolean hasUpperAlphabet,boolean hasNumber,boolean hasSymbol){
        if(len < 4){
            throw new IllegalArgumentException("字符串长度不能小于4");
        }
        //数组，用于存放随机字符
        char[] chArr = new char[len];
        int type = 0;
        if(hasLowerAlphabet) {
        	chArr[type] = (char)('a' + StdRandom.uniform(0,26));
        	type++;
        }
        if(hasUpperAlphabet) {
        	chArr[type] = (char)('A' + StdRandom.uniform(0,26));
        	type++;
        }
        if(hasNumber) {
        	chArr[type] = (char)('0' + StdRandom.uniform(0,10));
        	type++;
        }
        if(hasSymbol) {
        	int select = StdRandom.uniform(0,4);
        	if(select==0) {
        		chArr[type] = (char)('!' + StdRandom.uniform(0,15));
        	}else if(select==1) {
        		chArr[type] = (char)(':' + StdRandom.uniform(0,7));
        	}else if(select==2) {
        		chArr[type] = (char)('[' + StdRandom.uniform(0,6));
        	}else {
        		chArr[type] = (char)('{' + StdRandom.uniform(0,4));
        	}
        	type++;
        }
        
        StringBuffer stringBuffer = new StringBuffer();
        for(int i=33;i<=126;i++) {
        	if((int)'a'<=i && i<=(int)'z' && hasLowerAlphabet) {
        		stringBuffer.append((char)i);
        	}
        	if((int)'A'<=i && i<=(int)'Z' && hasUpperAlphabet) {
        		stringBuffer.append((char)i);
        	}
        	if((int)'0'<=i && i<=(int)'9' && hasNumber) {
        		stringBuffer.append((char)i);
        	}
        	if(((33<=i&&i<=47)||(58<=i&&i<=64)||(91<=i&&i<=96)||(123<=i&&i<=126))&&hasSymbol) {
        		stringBuffer.append((char)i);
        	}
        }
        
    
        char[] codes = stringBuffer.toString().toCharArray();
        //charArr[type..len-1]随机生成codes中的字符
        for(int i = type; i < len; i++){
            chArr[i] = codes[StdRandom.uniform(0,codes.length)];
        }
        
        //将数组chArr随机排序
        for(int i = 0; i < len; i++){
            int r = i + StdRandom.uniform(len - i);
            char temp = chArr[i];
            chArr[i] = chArr[r];
            chArr[r] = temp;
        }
        
        return new String(chArr);
    }
}