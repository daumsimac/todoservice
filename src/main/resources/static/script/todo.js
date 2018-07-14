'use strict';

function loadTodos () {
    todoApi.getTodos((errorMessage, response) => {
        if(errorMessage != null) {
            return alert('할일들을 불러오지 못하였습니다.(' + errorMessage + ')');
         }

         var referenceSelect = $('#todo-reference-select');
         for (var i in response.data.content) {
             referenceSelect.append($('<option>', {
                 value : response.data.content[i].id,
                 text : response.data.content[i].display_content
             }));
         }
    });
}

$(document).ready(() => {
    loadTodos();

    $('#add-todo-button').click(() => {
        var todoContent = $('#todo-input').val();

        if (!todoContent || todoContent == '') {
            return alert('할일을 입력해주세요.');
        }

        todoApi.createTodo({
            content : todoContent
        }, (errorMessage, response) => {
            if (errorMessage != null) {
                return alert('할일 등록에 실패하였습니다.(' + errorMessage + ')');
            }

            // TODO : reload list table
        });
    });
});