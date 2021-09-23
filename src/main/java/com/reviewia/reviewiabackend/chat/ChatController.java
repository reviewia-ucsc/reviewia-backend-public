package com.reviewia.reviewiabackend.chat;

import com.reviewia.reviewiabackend.chat.group.ChatGroup;
import com.reviewia.reviewiabackend.chat.group.ChatGroupService;
import com.reviewia.reviewiabackend.chat.message.Message;
import com.reviewia.reviewiabackend.chat.message.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ChatController {
    private ChatGroupService chatGroupService;
    private MessageService messageService;

    @GetMapping("/user/group")
    public ResponseEntity<ChatGroup> getGroupByCreatedUserEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(chatGroupService.findByCreatedUser(email));
    }

    @GetMapping("/user/group/{id}")
    public ResponseEntity<ChatGroup> getGroupById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(chatGroupService.findByGroupId(id));
    }

    @GetMapping("/user/group/all")
    public ResponseEntity<List<ChatGroup>> getUsersGroupsByEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(chatGroupService.findAllByUser(email));
    }

    @PostMapping("/user/group")
    public ResponseEntity<ChatGroup> createGroup(
            @RequestParam("email") String email,
            @RequestParam("post") Long post,
            @RequestBody ChatGroupRequest request
    ) {
        return ResponseEntity.status(201).body(chatGroupService.create(email, post, request.getEmails()));
    }

    @GetMapping("/user/group/deactivate")
    public ResponseEntity<?> deactivate(@RequestParam(required = false) String email, @RequestParam(required = false) Long id) {
        chatGroupService.deactivate(email, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/chat")
    public ResponseEntity<Message> createMessage(
            @RequestParam("email") String email,
            @RequestParam("group") Long group,
            @RequestBody Message message
    ) {
        return ResponseEntity.status(201).body(messageService.create(email, group, message));
    }

    @PostMapping("/user/group/add")
    public ResponseEntity<ChatGroup> addUsersToGroup(@RequestParam("id") Long id, @RequestBody ChatGroupRequest request) {
        return ResponseEntity.ok(chatGroupService.addUsersToGroup(id, request.getEmails()));
    }

    @PostMapping("/user/group/remove")
    public ResponseEntity<ChatGroup> removeFromGroup(@RequestParam("id") Long id, @RequestBody ChatGroupRequest request) {
        return ResponseEntity.ok(chatGroupService.removeUsersFromGroup(id, request.getEmails()));
    }
}
