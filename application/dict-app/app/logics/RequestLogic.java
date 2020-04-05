package logics;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import daos.UserRequestDao;
import daos.UserRequestDaoMongoImpl;
import objects.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import utilities.JsonUtil;
import utilities.ShobdoLogger;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RequestLogic {
    private final WordLogic wordLogic;
    private final UserRequestDao userRequestDao;

    private static final ShobdoLogger logger = new ShobdoLogger(RequestLogic.class);

    public static RequestLogic createMongoBackedRequestLogic() {
        return new RequestLogic(WordLogic.createMongoBackedWordLogic(), new UserRequestDaoMongoImpl());
    }

    public RequestLogic(final WordLogic wordLogic,
                        final UserRequestDao userRequestDao) {
        this.wordLogic = wordLogic;
        this.userRequestDao = userRequestDao;
    }

    private String generateUserRequestId() {
        return String.format("%s-%s", Constants.REQUEST_ID_PREFIX, UUID.randomUUID());
    }

    private UserRequest getRequest(@NotNull final String requestId) {
        if (requestId == null || requestId.trim().length() == 0) {
            throw new IllegalArgumentException(Constants.ID_NULLOREMPTY + requestId);
        }
        return userRequestDao.get(requestId);
    }


    //The following creates a word update request and returns the id
    public String createUserRequestForWordCreation(final JsonNode wordJNode) {

        final Word createWord = (Word) JsonUtil.jNodeToObject(wordJNode, Word.class);
        wordLogic.validateCreateWordObject(createWord);

        final UserRequest createRequest = UserRequest.builder()
            .id(generateUserRequestId())
            .targetInfo(new HashMap<>())
            .targetType(TargetType.WORD)
            .operation(RequestOperation.CREATE)
            .requestBody(wordJNode)
            .build();

        return userRequestDao.create(createRequest).getId();
    }


    //The following creates a word update request and returns the id
    private String createUserRequestForWordUpdate(final String wordId, final JsonNode wordJsonNode) {

        final Word updateWord = (Word) JsonUtil.jNodeToObject(wordJsonNode, Word.class);
        updateWord.setId(wordId);
        wordLogic.validateUpdateWordObject(updateWord);

        final Map<TargetType, String> targetInfo = new HashMap<>();
        targetInfo.put(TargetType.WORD, updateWord.getId());

        final UserRequest updateRequest = UserRequest.builder()
            .id(generateUserRequestId())
            .targetInfo(targetInfo)
            .targetType(TargetType.WORD)
            .operation(RequestOperation.UPDATE)
            .requestBody(JsonUtil.objectToJNode(updateWord))
            .build();

        return userRequestDao.create(updateRequest).getId();
    }

    private String createUserRequestForWordDeletion(final String wordId) {

        final Map<TargetType, String> targetInfo = new HashMap<>();
        targetInfo.put(TargetType.WORD, wordId);

        final UserRequest deleteRequest = UserRequest.builder()
            .id(generateUserRequestId())
            .targetInfo(targetInfo)
            .targetType(TargetType.WORD)
            .operation(RequestOperation.DELETE)
            .build();

        return userRequestDao.create(deleteRequest).getId();
    }

    //todo make transactional
    //approveUserRequest applies the requested changes to a word
    public boolean approveUserRequest(final String requestId) {

        final UserRequest request  = getRequest(requestId);
        final String wordId;
        switch (request.getTargetType()) {
            case WORD:
                switch (request.getOperation()) {
                    case CREATE:
                        wordLogic.createWord(request.getRequestBody());
                        break;
                    case UPDATE:
                        wordId = Preconditions.checkNotNull(request.getTargetInfo().get(TargetType.WORD));
                        wordLogic.updateWord(wordId, request.getRequestBody());
                        break;
                    case DELETE:
                        wordId = Preconditions.checkNotNull(request.getTargetInfo().get(TargetType.WORD));
                        wordLogic.deleteWord(wordId);
                        break;
                }
                break;
            case MEANING:
                final String meaningId;
                switch (request.getOperation()) {
                    case CREATE:
                        wordId = Preconditions.checkNotNull(request.getTargetInfo().get(TargetType.WORD));
                        wordLogic.createMeaning(wordId, request.getRequestBody());
                        break;
                    case UPDATE:
                        wordId = Preconditions.checkNotNull(request.getTargetInfo().get(TargetType.WORD));
                        meaningId = Preconditions.checkNotNull(request.getTargetInfo().get(TargetType.MEANING));
                        wordLogic.updateMeaning(wordId, meaningId, request.getRequestBody());
                        break;
                    case DELETE:
                        wordId = Preconditions.checkNotNull(request.getTargetInfo().get(TargetType.WORD));
                        meaningId = Preconditions.checkNotNull(request.getTargetInfo().get(TargetType.MEANING));
                        wordLogic.deleteMeaning(wordId, meaningId);
                        break;
                }
                break;
        }
        saveRequestAsMerged("deleterId", request);
        return true;
    }

    //The following creates a word update request and returns the id
    public String createUserRequestForMeaningCreation(final String wordId, final JsonNode meaningJNode) {

        final Meaning meaning = (Meaning) JsonUtil.jNodeToObject(meaningJNode, Meaning.class);

        wordLogic.validateCreateMeaningObject(wordId, meaning);

        final Map<TargetType, String> targetInfo = new HashMap<>();
        targetInfo.put(TargetType.WORD, wordId);

        final UserRequest createRequest = UserRequest.builder()
            .id(generateUserRequestId())
            .targetInfo(targetInfo)
            .targetType(TargetType.MEANING)
            .operation(RequestOperation.CREATE)
            .requestBody(JsonUtil.objectToJNode(meaning))
            .build();

        return userRequestDao.create(createRequest).getId();
    }

    //The following creates a word update request and returns the id
    private String createUserRequestForMeaningUpdate(final String wordId, final JsonNode meaningJNode) {

        final Meaning updateMeaning = (Meaning) JsonUtil.jNodeToObject(meaningJNode, Meaning.class);
        wordLogic.validateUpdateMeaningObject(wordId, updateMeaning);

        final Map<TargetType, String> targetInfo = new HashMap<>();
        targetInfo.put(TargetType.WORD, wordId);
        targetInfo.put(TargetType.MEANING, updateMeaning.getId());

        final UserRequest updateRequest = UserRequest.builder()
            .id(generateUserRequestId())
            .targetInfo(targetInfo)
            .targetType(TargetType.MEANING)
            .operation(RequestOperation.UPDATE)
            .requestBody(JsonUtil.objectToJNode(updateMeaning))
            .build();

        return userRequestDao.create(updateRequest).getId();
    }

    //The following creates a word update request and returns the id
    private String createUserRequestForMeaningUpdate(final String wordId, final String meangingId) {

        final Map<TargetType, String> targetInfo = new HashMap<>();
        targetInfo.put(TargetType.WORD, wordId);
        targetInfo.put(TargetType.MEANING, meangingId);

        final UserRequest updateRequest = UserRequest.builder()
            .id(generateUserRequestId())
            .targetInfo(targetInfo)
            .targetType(TargetType.MEANING)
            .operation(RequestOperation.DELETE)
            .build();

        return userRequestDao.create(updateRequest).getId();
    }


    private void saveRequestAsMerged(final String approverId, final UserRequest request) {
        request.setStatus(EntityStatus.DELETED);
        request.setDeleterId(approverId);
        final String deletionDateString = (new DateTime(DateTimeZone.UTC)).toString();
        request.setDeletedDate(deletionDateString);
        userRequestDao.update(request);
    }

}