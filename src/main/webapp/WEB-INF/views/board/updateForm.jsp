<%@page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>

<%@include file="../layout/header.jsp"%>
<div class="container">
    <div class="form-group">
        <label for="category">카테고리 선택:</label>
        <select class="form-control" id="category">
            <c:forEach items="${categories}" var="category">
                <option value="${category.id}">${category.name}</option>
            </c:forEach>
        </select>
    </div>
    <form>
        <input type="hidden"id="id"value="${board.id}"/>
        <div class="form-group">
            <input value="${board.title}" type="text" class="form-control" placeholder="제목을 입력하세요" id="title">
        </div>

        <div class="form-group">
            <textarea  class="form-control summernote" rows="5" id="content">${board.content}</textarea>
        </div>

    </form>
    <button id="btn-board-update" class="btn btn-primary">글수정 완료</button>

</div>
<script>
    $('.summernote').summernote({
        placeholder: '내용을 입력하세요',
        tabsize: 2,
        height: 300
    });
</script>
<script src="/js/board.js"></script>
<%@include file="../layout/footer.jsp"%>


