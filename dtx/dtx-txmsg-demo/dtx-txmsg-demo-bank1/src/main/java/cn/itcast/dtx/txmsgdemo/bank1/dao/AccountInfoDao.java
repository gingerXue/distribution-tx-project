package cn.itcast.dtx.txmsgdemo.bank1.dao;

import cn.itcast.dtx.txmsgdemo.bank1.entity.AccountInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface AccountInfoDao {
    @Update("update account_info set account_balance=account_balance+#{amount} where account_no=#{accountNo}")
    int updateAccountBalance(@Param("accountNo") String accountNo, @Param("amount") Double amount);


    @Select("select * from account_info where where account_no=#{accountNo}")
    AccountInfo findByIdAccountNo(@Param("accountNo") String accountNo);


    @Select("select count(1) from de_duplication where tx_no = #{txNo}")
    int isExistTx(String txNo);


    @Insert("insert into de_duplication values(#{txNo},now());")
    int addTx(String txNo);


    @Insert("INSERT INTO account_info(id, account_name, account_no, account_password, account_balance) " +
            "VALUES (#{id}, #{accountName}, #{accountNo}, #{accountPassword}, #{accountBalance})")
    @SelectKey(before = false, keyColumn = "id", keyProperty = "id", resultType = Long.class,
            statement = "select last_insert_id()")
    int addAccount(AccountInfo accountInfo);

    @Insert({
            "<script>",
                "INSERT INTO account_info(id, account_name, account_no, account_password, account_balance) VALUES",
                "<foreach collection='accountInfos' item='item' index='index' separator=','>",
                    "(#{item.id}, #{item.accountName}, #{item.accountNo}, #{item.accountPassword}, #{item.accountBalance})",
                "</foreach>",
            "</script>"
    })
    int addAccountBatch(@Param("accountInfos") List<AccountInfo> accountInfos);

}
