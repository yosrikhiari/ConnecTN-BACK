package tn.esprit.spring.connectn.Services.Interfaces.groups;


import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Entities.groups.GroupType;
import tn.esprit.spring.connectn.Entities.groups.Group;
import tn.esprit.spring.connectn.Entities.groups.JoinStatus;

import java.util.List;
import java.util.Map;

public interface IGroupsService {
    Group createGroup(Group group);
    Group getGroupById(Long id);
    List<Group> getAllGroups();
    Group updateGroup(Long id, Group groupDetails);
    void deleteGroup(Long id);

    List<Group> getMyGroups(Long userId);
    List<Group> getGroupsByType(GroupType type);

    Group addMember(Long groupId, Long userId);
    Group removeMember(Long groupId, Long userId);
    Group approveRequest(Long groupId, Long userId);
    Group rejectRequest(Long groupId, Long userId);
    boolean canAccessChat(Long groupId, Long userId);
    Map<User, JoinStatus> getPendingRequests(Long groupId);
}