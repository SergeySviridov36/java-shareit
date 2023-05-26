package ru.practicum.shareit.item.comment;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        return comment;
    }

    public static CommentDtoResponse toCommentDtoResponse(Comment comment) {
        CommentDtoResponse responseDto = new CommentDtoResponse();
        responseDto.setId(comment.getId());
        responseDto.setText(comment.getText());
        responseDto.setAuthorName(comment.getAuthor().getName());
        responseDto.setCreated(comment.getCreated());
        return responseDto;
    }
}
