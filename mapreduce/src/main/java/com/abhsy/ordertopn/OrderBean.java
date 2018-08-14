package com.abhsy.ordertopn;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @program: abhsy-hadoop
 * @author: jikai.sun
 * @create: 2018-08-14
 **/
public class OrderBean implements WritableComparable<OrderBean> {
    private String orderNo;
    private Double money;

    @Override
    public int compareTo(OrderBean o) {
        return (int) (o.getMoney() - this.getMoney());
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(orderNo);
        out.writeDouble(money);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.orderNo = in.readUTF();
        this.money = in.readDouble();
    }

    @Override
    public String toString() {
        return "OrderBean{" +
                "orderNo='" + orderNo + '\'' +
                ", money=" + money +
                '}';
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public String getOrderNo() {

        return orderNo;
    }

    public Double getMoney() {
        return money;
    }

}
