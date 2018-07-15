'use strict';

let todoApi = (() => {
    const API_PREFIX = '/api/v1/todos';

    function _getTodos (page, callback) {
        $.ajax({
            url : API_PREFIX + '?page=' + page,
            method : 'get',
        }).then((response) => {
            callback(null, response);
        }).catch((response) => {
            let responseBody = response.responseJSON;
            let message = responseBody.message;
            callback(message, responseBody);
        });
    }

    function _createTodo (param, callback) {
        $.ajax({
            url : API_PREFIX,
            method : 'post',
            data: JSON.stringify(param),
            contentType : 'application/json',
            dataType : 'json'
        }).then((response) => {
            callback(null, response);
        }).catch((response) => {
            let responseBody = response.responseJSON;
            let message = responseBody.message;
            callback(message, responseBody);
        });
    }

    function _completeTodo (id, callback) {
        $.ajax({
            url : API_PREFIX + '/' + id + '/complete',
            method : 'post',
        }).then((response) => {
            callback(null, response);
        }).catch((response) => {
            let responseBody = response.responseJSON;
            let message = responseBody.message;
            callback(message, responseBody);
        })
    }

    return {
        getTodos : _getTodos,
        createTodo : _createTodo,
        completeTodo : _completeTodo
    };
})();