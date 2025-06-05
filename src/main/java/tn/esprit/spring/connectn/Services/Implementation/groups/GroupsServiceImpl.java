package tn.esprit.spring.connectn.Services.Implementation.groups;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Entities.groups.GroupType;
import tn.esprit.spring.connectn.Entities.groups.Group;
import tn.esprit.spring.connectn.Entities.groups.JoinStatus;
import tn.esprit.spring.connectn.Repository.UserRepository;
import tn.esprit.spring.connectn.Repository.groups.GroupsRepository;
import tn.esprit.spring.connectn.Services.Interfaces.groups.IGroupsService;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class GroupsServiceImpl implements IGroupsService {

    private final GroupsRepository groupRepository;
    private final UserRepository userRepository;

    public GroupsServiceImpl(GroupsRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public Group createGroup(Group group) {
        if (group.getAdmin() == null || group.getAdmin().getId() == null) {  // Changé getUserId() en getId()
            throw new RuntimeException("Admin user ID is required");
        }

        User creator = userRepository.findById(group.getAdmin().getId())  // Changé getUserId() en getId()
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (group.getType() == GroupType.PRIVATE && creator.getPoints() <= 100) {
            throw new RuntimeException("Only users with more than 100 points can create private groups");
        }

        group.setAdmin(creator);
        if (group.getType() == GroupType.PRIVATE) {
            group.getMembers().add(creator);
        }
        return groupRepository.save(group);
    }

    @Override
    public Group getGroupById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + id));
    }

    @Override
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public List<Group> getMyGroups(Long id) {
        return groupRepository.findByMembersContaining(id);
    }

    @Override
    public Group updateGroup(Long id, Group groupDetails) {
        Group group = getGroupById(id);
        if (!group.getAdmin().getId().equals(groupDetails.getAdmin().getId())) {  // Changé getUserId() en getId()
            throw new RuntimeException("Only group admin can update the group");
        }
        group.setName(groupDetails.getName());
        group.setDescription(groupDetails.getDescription());
        group.setType(groupDetails.getType());
        return groupRepository.save(group);
    }

    @Override
    public void deleteGroup(Long id) {
        Group group = getGroupById(id);
        groupRepository.delete(group);
    }

    @Override
    public Group addMember(Long groupId, Long userId) {
        Group group = getGroupById(groupId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (group.getType() == GroupType.PUBLIC) {
            if (!group.getMembers().contains(user)) {
                group.getMembers().add(user);
                return groupRepository.save(group);
            }
            return group;
        } else {
            group.getPendingRequests().put(user, JoinStatus.PENDING);
            return groupRepository.save(group);
        }
    }

    @Override
    public Group removeMember(Long groupId, Long userId) {
        Group group = getGroupById(groupId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!group.getAdmin().getId().equals(userId)) {  // Changé getUserId() en getId()
            group.getMembers().remove(user);
            return groupRepository.save(group);
        }
        throw new RuntimeException("Admin cannot be removed from group");
    }

    @Override
    public Group approveRequest(Long groupId, Long userId) {
        Group group = getGroupById(groupId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (group.getPendingRequests().containsKey(user)) {
            // Ajouter le membre
            group.getMembers().add(user);
            // Supprimer la demande (pas juste mettre à jour le statut)
            group.getPendingRequests().remove(user);
            return groupRepository.save(group);
        }
        throw new RuntimeException("No pending request for this user");
    }

    @Override
    public Group rejectRequest(Long groupId, Long userId) {
        Group group = getGroupById(groupId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (group.getPendingRequests().containsKey(user)) {
            // Supprimer complètement la demande
            group.getPendingRequests().remove(user);
            return groupRepository.save(group);
        }
        throw new RuntimeException("No pending request for this user");
    }

    @Override
    public boolean canAccessChat(Long groupId, Long userId) {
        Group group = getGroupById(groupId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return group.getType() == GroupType.PUBLIC ||
                group.getMembers().contains(user) ||
                group.getAdmin().getId().equals(userId);  // Changé getUserId() en getId()
    }

    @Override
    public Map<User, JoinStatus> getPendingRequests(Long groupId) {
        Group group = getGroupById(groupId);
        return group.getPendingRequests();
    }

    public List<Group> getGroupsByType(GroupType type) {
        return groupRepository.findByType(type);
    }
}