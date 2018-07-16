'use strict';

function completeTodo (todoId) {
    todoApi.completeTodo(todoId, (errorMessage, response) => {
        if (errorMessage != null) {
            return alert('완료처리에 실패하였습니다.(' + errorMessage + ')');
        }

        $('#todo-list').DataTable().ajax.reload(null, false);
    });
}

function injectTodoIdToModal (todoId, content) {
    console.log('todoId = ' + todoId + ', content = ' + content);
    $(".modal-body #hiddenValue").val(todoId);
    $('#todo-modify-input').val(content);
}

function deleteTodo (todoId) {
    todoApi.deleteTodo(todoId, (errorMessage, response) => {
        if (errorMessage != null) {
            return alert('할일 삭제에 실패하였습니다.(' + errorMessage + ')');
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
                    if (response.data.content[i].completed_at != null) continue;

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
        columnDefs : [
            {
                targets : 0,
                className : 'text-center',
                width : '10%'
            },
            {
                targets : 1,
                className : 'text-center',
                width : '30%'
            },
            {
                targets : 2,
                className : 'text-center',
                width : '15%'
            },
            {
                targets : 3,
                className : 'text-center',
                width : '15%'
            },
            {
                targets : 4,
                className : 'text-center',
                width : '15%'
            },
            {
                targets : 5,
                className : 'text-center',
                witdh : '15%'
            }
        ],
        columns: [
            {
                data: 'id',
                width : '10%'
            },
            {
                data: 'display_content',
                render : (data, type, row, meta) => {
                    return '<span><button type="button" class="btn btn-light btn-block modify-todo-button btn-sm" ' +
                        'data-toggle="modal" data-target="#modifyTodoModal" ' +
                        ' onclick="injectTodoIdToModal('+row['id']+ ',\'' + row['content'] + '\');">'+ data + '</button></span>';
                },
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
                        return '<button type="button" class="btn btn-success complete-todo-button" onclick="completeTodo('+row['id']+');">완료하기</button>';
                    }
                    else {
                        return data;
                    }
                }
            },
            {
                render : (data, type, row, meta) => {
                    return '<button type="button" class="btn btn-danger delete-todo-button" onclick="deleteTodo('+row['id']+');">삭제하기</button>';
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

    $(document).on('click', '#modify-todo-button', () => {
        var todoContent = $('#todo-modify-input').val();

        if (!todoContent || todoContent == '') {
            return alert('할일을 입력해주세요.');
        }

        var todoId = $('#hiddenValue').val();

        todoApi.updateTodo(todoId, {
            content : todoContent
        }, (errorMessage, response) => {
            if (errorMessage != null) {
                return alert('할일 수정에 실패하였습니다.(' + errorMessage + ')');
            }

            $('#todo-modify-input').val('');
            $('#hiddenValue').val('');
            $('#modifyTodoModal').modal('toggle');
            $('#todo-list').DataTable().ajax.reload(null, false);
        });
    });
});