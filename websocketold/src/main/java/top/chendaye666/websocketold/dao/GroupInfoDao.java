package top.chendaye666.websocketold.dao;


import top.chendaye666.websocketold.model.po.GroupInfo;

public interface GroupInfoDao {

    void loadGroupInfo();
    
    GroupInfo getByGroupId(String groupId);
}
