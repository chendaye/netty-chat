package top.chendaye666.websocket.dao.mock;


import top.chendaye666.websocket.model.po.GroupInfo;

public interface GroupInfoDao {

    void loadGroupInfo();
    
    GroupInfo getByGroupId(String groupId);
}
