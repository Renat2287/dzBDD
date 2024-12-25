package ru.netology.test;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage2;

import static com.codeborne.selenide.Selenide.$;
import static ru.netology.data.DataHelper.getAuthInfo;

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
        firstCardInfo = DataHelper.getFirstCardInfo();
        secondCardInfo = DataHelper.getSecondCardInfo();
    }

    @Test
    void transferFromTheFirstCardToTheSecond() {
        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        int amount = initialFirstCardBalance / 2;

        var transferPage = dashboardPage.selectCardForTransfer(secondCardInfo);
        dashboardPage = transferPage.transfer(firstCardInfo.getCardNumber(), amount);
        dashboardPage.assertCardBalance(firstCardInfo, initialFirstCardBalance - amount);
        dashboardPage.assertCardBalance(secondCardInfo, initialSecondCardBalance + amount);
    }

    @Test
    void transferFromTheSecondCardToTheFirst() {
        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        int amount = initialSecondCardBalance / 2;

        var transferPage = dashboardPage.selectCardForTransfer(firstCardInfo);
        dashboardPage = transferPage.transfer(secondCardInfo.getCardNumber(), amount);
        dashboardPage.assertCardBalance(firstCardInfo, initialFirstCardBalance + amount);
        dashboardPage.assertCardBalance(secondCardInfo, initialSecondCardBalance - amount);
    }
}
