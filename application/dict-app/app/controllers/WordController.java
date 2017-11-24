package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import logics.WordLogic;
import objects.Word;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import utilities.DictUtil;
import utilities.JsonUtil;
import utilities.LogPrint;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by tahsinkabir on 5/28/16.
 */
public class WordController extends Controller {

    private final WordLogic wordLogic = WordLogic.factory();
    private static LogPrint log = new LogPrint(WordController.class);

    public Result index() {

        String welcome = "বাংলা অভিধান এ স্বাগতম!";
        log.info(welcome);
        return ok(welcome);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result searchWordsBySpelling() {

        String searchString = "";
        JsonNode json = request().body().asJson();
        Set<String> wordSpellings = new HashSet<>();

        try {

            searchString = json.get("searchString").asText();

            if (searchString.length() > 0)
                wordSpellings = wordLogic.searchWords(searchString);

        } catch (Exception ex) {

            log.info("WC001 Property 'searchString' not found in the json body. Body found:" + json.textValue());
            log.info("WC002 Exception Stacktrace:" + ex.getStackTrace().toString());
            return badRequest();
        }

        return ok(Json.toJson(wordSpellings));
    }

    //CREATE
    @BodyParser.Of(BodyParser.Json.class)
    public Result createWord() {

        JsonNode json = request().body().asJson();
        Word word = (Word) JsonUtil.jsonNodeToObject(json, Word.class);
        wordLogic.createWord(word);
        return ok();
    }

    //READ
    @BodyParser.Of(BodyParser.Json.class)
    public Result getWordByWordIdPost() {

        JsonNode json = request().body().asJson();
        String wordId;

        try {

            wordId = json.get("wordId").asText();

        } catch (Exception ex) {

            log.info("WC005 Property 'wordId' not found in the json body. Body found:" + json.textValue());
            log.info("WC006 Exception Stacktrace:" + ex.getStackTrace().toString());
            return badRequest();
        }

        return getWordByWordId(wordId);
    }

    public Result getWordByWordId(String wordId) {

        Word word = wordLogic.getWordByWordId(wordId);

        if (word == null)
            return ok("No word found for wordId:\"" + wordId + "\"");
        else
            return ok(Json.toJson(word));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getWordBySpellingPost() {

        String wordSpelling;
        JsonNode json = request().body().asJson();

        try {

            wordSpelling = json.get("wordSpelling").asText();

        } catch (Exception ex) {

            log.info("WC003 Property 'wordSpelling' not found in the json body. Body found:" + json.textValue());
            log.info("WC004 Exception Stacktrace:" + ex.getStackTrace().toString());
            return badRequest();
        }

        return getWordBySpelling(wordSpelling);
    }

    public Result getWordBySpelling(String wordSpelling) {

        Word word = wordLogic.getWordBySpelling(wordSpelling);

        if (word == null)
            return ok("No word found for spelling:\"" + wordSpelling + "\"");
        else
            return ok(Json.toJson(word));
    }

    //UPDATE TODO
    @BodyParser.Of(BodyParser.Json.class)
    public Result updateWord(String wordId) {

        JsonNode json = request().body().asJson();
        Word word = (Word) JsonUtil.jsonNodeToObject(json, Word.class);
        wordLogic.updateWord(wordId, word);
        return ok();
    }

    //DELETE TODO
    @BodyParser.Of(BodyParser.Json.class)
    public Result deleteWord(String wordId) {

        JsonNode json = request().body().asJson();
        log.info("Delete word with wordId:" + wordId);
        return ok();
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result listWords(String startWordId, Integer limit) {
        log.info("List words starting from wordId:" + startWordId + ", limit:" + limit);
        return ok();
    }

    /* Meaning related API */

    @BodyParser.Of(BodyParser.Json.class)
    public Result createMeaning(String wordId) {
        JsonNode json = request().body().asJson();
        log.info("Create meaning: " + json + " on word with wordId:" + wordId);
        wordLogic.createMeaning(wordId, null);
        return ok();
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getMeaning(String wordId, String meaningId) {
        log.info("Get meaning with meaningId:" + meaningId  + " of word with wordId:" + wordId);
        wordLogic.getMeaning(wordId, meaningId);
        return ok();
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result updateMeaning(String wordId, String meaningId) {
        JsonNode json = request().body().asJson();
        log.info("Update meaning with meaningId: " + meaningId + " with json:" + json
                + " on word with wordId:" + wordId);
        wordLogic.updateMeaning(wordId, null);
        return ok();
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result deleteMeaning(String wordId, String meaningId) {
        log.info("Delete meaning: " + meaningId + " on word with wordId:" + wordId);
        wordLogic.deleteMeaning(wordId, meaningId);
        return ok();
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result listMeanings(String wordId) {
        log.info("List meanings on word with wordId:" + wordId);
        wordLogic.listMeanings(wordId);
        return ok();
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result createRandomDictionary() { //remove this route for eventual deployment

        JsonNode json = request().body().asJson();

        int wordCount;

        try {

            wordCount = Integer.parseInt(json.get("wordCount").asText());

        } catch (Exception ex) {

            log.info("WC007 Property 'wordCount' not found in the json body. Body found:" + json.textValue());
            log.info("WC008 Exception Stacktrace:" + ex.getStackTrace().toString());

            return badRequest();
        }

        Set<Word> words = DictUtil.generateDictionaryWithRandomWords(wordCount);

        for (Word word : words)
            wordLogic.createWord(word);

        return ok("Generated and added " + wordCount + " random words on the dictionary!");
    }

}
