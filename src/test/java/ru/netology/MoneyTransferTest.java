package ru.netology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static ru.netology.DataHelper.getAuthInfo;

public class MoneyTransferTest {

    private DashboardPage dashboardPage;
    private DataHelper.CardInfo firstCardInfo;
    private DataHelper.CardInfo secondCardInfo;

    @BeforeEach
    void setup() {
        var info = DataHelper.getAuthInfo();
        var verificationCode = DataHelper.getVerificationCodeFor(info);
        Selenide.open("http://localhost:9999");
        var loginPage = new LoginPage2();
        var verificationPage = loginPage.validLogin(info);
        dashboardPage = verificationPage.validVerify(verificationCode);
        firstCardInfo = DataHelper.CardInfo.getFirstCardInfo();
        secondCardInfo = DataHelper.CardInfo.getSecondCardInfo();
    }

    @Test
    void transferFromTheFirstCardToTheSecond() {
        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        int amount = 2000;

        var transferPage = dashboardPage.selectCardForTransfer(secondCardInfo);
        dashboardPage = transferPage.transfer(firstCardInfo.getCardNumber(), amount);
        dashboardPage.getCard(firstCardInfo)
                .shouldHave(Condition.text("баланс: " + (initialFirstCardBalance - amount) + " р."));
        dashboardPage.getCard(secondCardInfo)
                .shouldHave(Condition.text("баланс: " + (initialSecondCardBalance + amount) + " р."));
    }

    @Test
    void transferFromTheSecondCardToTheFirst() {
        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        int amount = 2000;

        var reverseTransferPage = dashboardPage.selectCardForTransfer(firstCardInfo);
        dashboardPage = reverseTransferPage.transfer(secondCardInfo.getCardNumber(), amount);
        dashboardPage.getCard(firstCardInfo)
                .shouldHave(Condition.text("баланс: " + (initialFirstCardBalance + amount) + " р."));
        dashboardPage.getCard(secondCardInfo)
                .shouldHave(Condition.text("баланс: " + (initialSecondCardBalance - amount) + " р."));
    }
}
