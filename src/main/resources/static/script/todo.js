'use strict';

function completeTodo (todoId) {
    todoApi.completeTodo(todoId, (errorMessage, response) => {
        if (errorMessage != null) {
            return alert('완료처리에 실패하였습니다.(' + errorMessage + ')');
        }

        $('#todo-list').DataTable().ajax.reload(null, false);
    });
}

$(document).ready(() => {
    $('#todo-list').DataTable({
        processing : true,
        serverSide : true,
        ordering : false,
        searching : false,
        lengthChange : false,
        language : {
            emptyTable : '할일을 등록해주세요.',
            processing : '요청하신 작업을 처리중입니다.',
        },
        ajax: function (data, callback, settings) {
            let page = Math.floor(data.start / data.length) + 1;

            todoApi.getTodos(page, (errorMessage, response) => {
                if (errorMessage != null) {
                    alert('할일들을 불러오지 못하였습니다.(' + errorMessage + ')');
                    return false;
                }

                $('#todo-reference-select option').remove();
                var referenceSelect = $('#todo-reference-select');
                referenceSelect.append('<option value="">참조</option>');

                for (var i in response.data.content) {
                    referenceSelect.append($('<option>', {
                        value : response.data.content[i].id,
                        text : response.data.content[i].display_content
                    }));
                }

                callback({
                    recordsTotal : response.data.totalElements,
                    recordsFiltered : response.data.totalElements,
                    data: response.data.content
                });
            });
        },
        columns: [
            {
                data: 'id'
            },
            {
                data: 'display_content'
            },
            {
                data: 'created_at'
            },
            {
                data: 'updated_at'
            },
            {
                data: 'completed_at',
                render : (data, type, row, meta) => {
                    if (data == null) {
                        return '<button type="button" class="btn btn-success complete-todo-button" onclick="completeTodo('+row['id']+')">완료하기</button>';
                    }
                    else {
                        return data;
                    }
                }
            }
        ]
    });

    $('#add-todo-button').click(() => {
        var todoContent = $('#todo-input').val();

        if (!todoContent || todoContent == '') {
            return alert('할일을 입력해주세요.');
        }

        var parentId = $('#todo-reference-select').val();

        todoApi.createTodo({
            content : todoContent,
            parent_id : parentId
        }, (errorMessage, response) => {
            if (errorMessage != null) {
                return alert('할일 등록에 실패하였습니다.(' + errorMessage + ')');
            }

            $('#todo-input').val('');
            $('#todo-list').DataTable().ajax.reload(null, false);
        });
    });
});