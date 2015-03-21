package org.namelessrom.ota.requests;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import junit.framework.TestCase;

import org.namelessrom.ota.changelog.Change;

public class ChangesJsonArrayRequestTest extends TestCase {

    public void testParseValidJson() throws Exception {
        final String jsonValid =
                "[" +
                        "   {" +
                        "     \"id\": \"NamelessRom%2Fandroid_packages_apps_Screencast~n-2.0~I0f08378bd382f5ab55f4d37cfe0ee2169e9ead54\"," +
                        "     \"project\": \"NamelessRom/android_packages_apps_Screencast\"," +
                        "     \"branch\": \"n-2.0\"," +
                        "     \"hashtags\": []," +
                        "     \"change_id\": \"I0f08378bd382f5ab55f4d37cfe0ee2169e9ead54\"," +
                        "     \"subject\": \"bump i-frame-interval from 3 to 5\"," +
                        "     \"status\": \"NEW\"," +
                        "     \"created\": \"2015-02-23 21:55:05.204000000\"," +
                        "     \"updated\": \"2015-02-23 21:58:42.422000000\"," +
                        "     \"mergeable\": true," +
                        "     \"insertions\": 1," +
                        "     \"deletions\": 1," +
                        "     \"_number\": 16117," +
                        "     \"owner\": {" +
                        "     \"_account_id\": 1000000" +
                        "     }" +
                        "   }" +
                        " ]";

        final Change[] changesValid = new Gson().fromJson(jsonValid, Change[].class);
        assertEquals(changesValid.length, 1);
    }

    public void testParseGerritJson() throws Exception {
        final String MAGIC = ")]}'";
        final String jsonGerrit =
                " [" +
                        "   {" +
                        "     \"id\": \"NamelessRom%2Fandroid_packages_apps_Screencast~n-2.0~I0f08378bd382f5ab55f4d37cfe0ee2169e9ead54\"," +
                        "     \"project\": \"NamelessRom/android_packages_apps_Screencast\"," +
                        "     \"branch\": \"n-2.0\"," +
                        "     \"hashtags\": []," +
                        "     \"change_id\": \"I0f08378bd382f5ab55f4d37cfe0ee2169e9ead54\"," +
                        "     \"subject\": \"bump i-frame-interval from 3 to 5\"," +
                        "     \"status\": \"NEW\"," +
                        "     \"created\": \"2015-02-23 21:55:05.204000000\"," +
                        "     \"updated\": \"2015-02-23 21:58:42.422000000\"," +
                        "     \"mergeable\": true," +
                        "     \"insertions\": 1," +
                        "     \"deletions\": 1," +
                        "     \"_number\": 16117," +
                        "     \"owner\": {" +
                        "     \"_account_id\": 1000000" +
                        "     }" +
                        "   }" +
                        " ]";
        final String jsonGerritInvalid = MAGIC + jsonGerrit;

        boolean success = true;
        Change[] changesGerrit;
        try {
            // this has to fail because gerrit inserts )]}' as magic
            changesGerrit = new Gson().fromJson(jsonGerritInvalid, Change[].class);
        } catch (JsonSyntaxException jse) {
            success = false;
            changesGerrit = null;
        }
        assertEquals(success, false);
        assertNull(changesGerrit);

        final String jsonGerritValid = jsonGerritInvalid.replaceFirst("\\)\\]\\}'", "").trim();
        assertEquals(jsonGerrit, jsonGerritValid);

        success = true;
        try {
            // this has to succeed because we removed the magic
            changesGerrit = new Gson().fromJson(jsonGerritValid, Change[].class);
        } catch (JsonSyntaxException jse) {
            success = false;
            changesGerrit = null;
        }
        assertEquals(success, true);
        assertNotNull(changesGerrit);
        assertEquals(changesGerrit.length, 1);

        final Change change = changesGerrit[0];
        assertNotNull(change);

        // verify if we correctly parsed the change
        assertEquals(change.project, "NamelessRom/android_packages_apps_Screencast");
        assertEquals(change.branch, "n-2.0");
        assertEquals(change.subject, "bump i-frame-interval from 3 to 5");
        assertEquals(change.created, "2015-02-23 21:55:05.204000000");
        assertEquals(change.updated, "2015-02-23 21:58:42.422000000");
        assertEquals(change.insertions, "1");
        assertEquals(change.deletions, "1");
    }

}
