/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hanteen.web.pro.model.mybatis.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;


/**
 * 用户的java类形式
 *
 * @author paida 派哒 zeyu.pzy@alibaba-inc.com
 */
public class MVCMybatisDemoUser implements Serializable, Comparable<MVCMybatisDemoUser> {
    @Override
    public int compareTo(@Nonnull MVCMybatisDemoUser o) {
        //返回值大于0 o在前 否者o在后
        return o.getAge() - this.age;
    }

    /**
     * 编号
     */
    private String id;

    /**
     * 用户名
     */
    private Integer age;

    /**
     * 密码
     */
    private Integer height;

    private Integer weight;


    public String getId() {
        return id;
    }

    public MVCMybatisDemoUser setId(String id) {
        this.id = id;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public MVCMybatisDemoUser setAge(Integer age)
    {
        this.age=age;
        return this;
    }


    public Integer getHeight() {
        return height;
    }

    public MVCMybatisDemoUser setHeight(Integer height)
    {
        this.height=height;
        return this;
    }

    public Integer getWeight() {
        return weight;
    }

    public MVCMybatisDemoUser setWeight(Integer weight)
    {
        this.weight=weight;
        return this;
    }

    public static void main(String[] args) {
        MVCMybatisDemoUser u1 = new MVCMybatisDemoUser();
        u1.setAge(12);
        MVCMybatisDemoUser u2 = new MVCMybatisDemoUser();
        u2.setAge(11);
        MVCMybatisDemoUser u3 = new MVCMybatisDemoUser();
        u3.setAge(7);
        List<MVCMybatisDemoUser> list = new ArrayList<>();
        list.add(u1);
        list.add(u2);
        list.add(u3);
        List<MVCMybatisDemoUser> collect = list.stream().sorted(new UserComparator()).collect(Collectors.toList());
        System.out.println(collect);
    }

    @Override
    public String toString() {
        return "MVCMybatisDemoUser{" +
                "id='" + id + '\'' +
                ", age=" + age +
                ", height=" + height +
                ", weight=" + weight +
                '}';
    }

    private static class UserComparator implements Comparator<MVCMybatisDemoUser> {

        @Override
        public int compare(MVCMybatisDemoUser o1, MVCMybatisDemoUser o2) {
            //返回负数o1在前
            return o1.getAge().compareTo(o2.getAge());
        }
    }
}