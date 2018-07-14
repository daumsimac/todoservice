'use strict';

$(document).ready(() => {
    $('#add-todo-button').click(() => {
        var todoContent = $('#todo-input').val();

        todoApi.createTodo({
            content : todoContent
        }, (errorMessage, response) => {
            if (errorMessage != null) {
                return alert('할일 등록에 실패하였습니다.(' + errorMessage + ')');
            }
        });
    });
});