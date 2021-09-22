package top.chendaye666;

import top.chendaye666.common.codec.InvocationPojo;

class ProtobufTest {
    public static void main(String[] args) throws Exception{
        InvocationPojo.Invocation student = InvocationPojo.Invocation.newBuilder().setType("张三").setMessage("protobuf").build();

        //将对象转译成字节数组,序列化
        byte[] student2ByteArray = student.toByteArray();

        //将字节数组转译成对象,反序列化
        InvocationPojo.Invocation student2 = InvocationPojo.Invocation.parseFrom(student2ByteArray);

        System.out.println(student2.getType());
        System.out.println(student2.getMessage());
    }
}
