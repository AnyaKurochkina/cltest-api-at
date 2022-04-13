package steps.accountManager;

import core.helper.JsonHelper;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import steps.Steps;

import java.util.Objects;

import static core.helper.Configure.AccountManagerURL;

@Log4j2
public class AccountSteps extends Steps {

    @Step("Перевод со счета {from} на счет {to} суммы {amount} c комментарием {reason}")
    public static void transferMoney(String from, String to, String amount, String reason) {
        JsonHelper.getJsonTemplate("/accountManager/transaction.json")
                .set("$.from_account_id", Objects.requireNonNull(from))
                .set("$.to_account_id", Objects.requireNonNull(to))
                .set("$.amount", Objects.requireNonNull(amount))
                .set("$.reason", Objects.requireNonNull(reason))
                .send(AccountManagerURL)
                .post("/api/v1/organizations/vtb/accounts/transfers")
                .assertStatus(200);
    }

    @Step("Запрос текущего баланса для папки {folderId}")
    public static Float getCurrentBalance(String folderId) {
        String res = new Http(AccountManagerURL)
                .get("/api/v1/folders/{}/accounts?force_spent_update=1", folderId)
                .assertStatus(200)
                .jsonPath()
                .getString("account.current_balance");
        return Float.valueOf(Objects.requireNonNull(res));
    }

    public static String getAccountIdByContext(String context) {
        log.info("Получение account_id для контекста - " + Objects.requireNonNull(context));
        String account_id = null;
        int total_count = new Http(AccountManagerURL)
                .get("/api/v1/organizations/vtb/accounts")
                .assertStatus(200)
                .jsonPath()
                .get("meta.total_count");
        int countOfIteration = total_count / 100 + 1;
        for (int i = 1; i <= countOfIteration; i++) {
            account_id = new Http(AccountManagerURL)
                    .get("/api/v1/organizations/vtb/accounts?page=" + i + "&per_page=100")
                    .assertStatus(200)
                    .jsonPath()
                    .get(String.format("list.find{it.name.contains('%s') || it.name.contains('%s')}.account_id", context.toUpperCase(), context.toLowerCase()));
            if (account_id != null)
                break;
        }
        return account_id;
    }
}
