'use strict';

let todoApi = (() => {
    const API_PREFIX = '/api/v1/todos/';

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
            let responseBody = response.responseBody;
            let message = responseBody.message;
            callback(message, responseBody);
        });
    }

    return {
        createTodo : _createTodo
    };
})();