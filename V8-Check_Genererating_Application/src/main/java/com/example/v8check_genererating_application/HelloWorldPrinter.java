package com.example.v8check_genererating_application;

import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Hashtable;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelloWorldPrinter implements Printable {

    private static final String[] ones = {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
    private static final String[] tens = {"ten", "eleven", "tweleve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eightteen", "nineteen"};
    private static final String[] tensPlace = {"twenty", "thirty", "fourty", "fifty", "sixty", "seventy", "eighty", "ninety"};
    private static final String[] decimalPlace = {"", "thousand "}; //empty string is neccessary for values less than one thousand
    // Name Variables
    final int PayToOrderY = 32; // 25*1.5 int division
    final int PayToOrderX = 0;
    // Money as Double
    final int MoneyDoubleY = PayToOrderY + 5;
    final int MoneyDoubleX = 430; // 17*25 + padding
    // Money as String
    final int MoneyStringY = 52;
    final int MoneyStringX = 0;
    // Address Information
    final int AddressY = 80;
    final int AddressX = 0;
    // Signature
    final int SignatureY = 60;
    final int SignatureX = 275;
    // Date
    final int DateY = 50;
    final int DateX = 400;
    // Memo
    final int MemoY = 120;
    final int MemoX = 0;
    // Block
    final int BlockY = 260;
    final int BlockX = 0;
    CheckController2.Check check = new CheckController2.Check(0, Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now()), BigDecimal.valueOf(5645), BigDecimal.valueOf(0.52), BigDecimal.valueOf(1500), BigDecimal.valueOf(.1), BigDecimal.valueOf(.01), BigDecimal.valueOf(.01), BigDecimal.valueOf(.01), "My memmo is galdkjafjl ;akja a;a jfalkf ald;a;djfkajd; ai;d ;dfjakd kdj;fakjd ad;jdjeia;jdkf d kaj; jadkf rjwk 4nr; ;ioa", 0);
    EmployeeController.Employee employee = new EmployeeController.Employee("Dad", "1234567890", "123456", "123456", "123456", "458 48 8554", 10);
    BigDecimal ytdHours = BigDecimal.valueOf(100);
    BigDecimal ytdRevenue = BigDecimal.valueOf(1020);
    BigDecimal ytdOvertimeHours = BigDecimal.valueOf(1040);
    BigDecimal ytdOvertimeRevenue = BigDecimal.valueOf(1700);
    BigDecimal ytdTotalRevenue = BigDecimal.valueOf(8100);
    BigDecimal ytdDeduction = BigDecimal.valueOf(1090);

    /*final int change = 10;*/
    BigDecimal ytdIncome = BigDecimal.valueOf(1009);
    BigDecimal ytdSSN = BigDecimal.valueOf(9100);
    BigDecimal ytdTax = BigDecimal.valueOf(1900);
    BigDecimal ytdMedicare = BigDecimal.valueOf(109);

    public static void main(String[] args) {

        new HelloWorldPrinter().print();
    }

    public static void print(CheckController2.Check check,
                             EmployeeController.Employee employee,
                             BigDecimal ytdHours,
                             BigDecimal ytdRevenue,
                             BigDecimal ytdOvertimeHours,
                             BigDecimal ytdOvertimeRevenue,
                             BigDecimal ytdTotalRevenue,
                             BigDecimal ytdDeduction,
                             BigDecimal ytdIncome,
                             BigDecimal ytdSSN,
                             BigDecimal ytdTax,
                             BigDecimal ytdMedicare) {
        var hw = new HelloWorldPrinter();
        if (check == null) throw new RuntimeException("Check cannot be null");
        hw.check = check;
        hw.employee = employee;
        hw.ytdHours = ytdHours;
        hw.ytdRevenue = ytdRevenue;
        hw.ytdOvertimeHours = ytdOvertimeHours;
        hw.ytdOvertimeRevenue = ytdOvertimeRevenue;
        hw.ytdTotalRevenue = ytdTotalRevenue;
        hw.ytdDeduction = ytdDeduction;
        hw.ytdIncome = ytdIncome;
        hw.ytdSSN = ytdSSN;
        hw.ytdTax = ytdTax;
        hw.ytdMedicare = ytdMedicare;
        hw.print();
    }

    private static String convertToLongName(BigDecimal d, boolean checkDecimalPlace, int digitLocation) throws Exception {
        if (d.compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("Paying someone negative money isn't possible");
        } else if (d.compareTo(new BigDecimal("0.01")) < 0) {
            return "";
        } else if (d.compareTo(new BigDecimal("1000000")) >= 0) {
            throw new Exception("Can't convert values greater than or equal to 1 million. Please enter lower values");
        }

        String value = "";

        int temp = d.remainder(new BigDecimal("1000")).intValue();
        int hundred = temp / 100; //Keeps only the value at the hundreds place

        int temp2 = temp - hundred * 100;
        if (hundred < 10 && hundred > 0) {
            value += (temp2 == 0) ? ones[hundred] + " hundred " : ones[hundred] + " hundred and ";
        }
        int temp3 = temp2;
        if (temp2 < 20 && temp2 >= 10) {
            value += tens[temp2 - 10] + " ";
            temp3 = 0;
        } else if (temp2 >= 20) {
            temp3 = temp2 % 10; //keeps only the value of the ones place
            temp2 = temp2 / 10; //keeps the values of the tens place
            value += (temp3 == 0) ? tensPlace[temp2 - 2] + " " : tensPlace[temp2 - 2] + " - ";
        }

        if (temp3 > 0) {
            value += ones[temp3] + " ";
        }

        String cents = "";
        if (checkDecimalPlace) {
            temp2 = d.remainder(BigDecimal.ONE).multiply(new BigDecimal("100")).intValue();
            temp3 = temp2;
            if (temp2 < 20 && temp2 >= 10) {
                cents += tens[temp2 - 10] + " ";
                temp3 = 0;
            } else if (temp2 >= 20) {
                temp3 = temp2 % 10; //keeps only the value of the ones place
                temp2 = temp2 / 10; //keeps the values of the tens place
                cents += (temp3 == 0) ? tensPlace[temp2 - 2] + " " : tensPlace[temp2 - 2] + " - ";
            }

            if (temp3 == 0 && temp2 > 0) {
                cents += "";
            } else if (temp3 >= 0) {
                cents += ones[temp3] + " ";
            }
        }

        if (digitLocation == 0) {
            return convertToLongName(d.divideToIntegralValue(new BigDecimal("1000")), false, digitLocation + 1) + value + decimalPlace[digitLocation] + "dollars" + " and " + cents + "cents";
        }
        return convertToLongName(d.divideToIntegralValue(new BigDecimal("1000")), false, digitLocation + 1) + value + decimalPlace[digitLocation];
    }

    private static String moneyToString(BigDecimal d) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        return format.format(d);
    }

    public int print(Graphics g, PageFormat pf, int page) {

        if (page > 0) { /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }

        /* User (0,0) is typically outside the imageable area, so we must
         * translate by the X and Y values in the PageFormat to avoid clipping
         */
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        g.setFont(new Font(Font.DIALOG, Font.PLAIN, 8));

        g.setColor(Color.BLACK);

        var twoDecimalPlaceNumber = check.income().setScale(2, RoundingMode.CEILING);
        Hashtable<Pair<Integer, Integer>, String> table = new Hashtable<>();
        table.put(new Pair<>(PayToOrderX, PayToOrderY), employee.getName());
        table.put(new Pair<>(MoneyDoubleX, MoneyDoubleY), twoDecimalPlaceNumber.toString());

        try {
            table.put(new Pair<>(MoneyStringX, MoneyStringY), convertToLongName(twoDecimalPlaceNumber, true, 0));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        table.put(new Pair<>(AddressX, AddressY), employee.getName());
        table.put(new Pair<>(AddressX, AddressY + 10), employee.getAddress());
        table.put(new Pair<>(AddressX, AddressY + 20), employee.getCity() + ", " + employee.getState() + " " + employee.getZipCode());

        BufferedImage img;
        try {
            FileInputStream fileInputStream = new FileInputStream("/home/kendricks/IdeaProjects/CheckGeneratingApplicationV6/src/main/resources/com/camillebusinesses/checks/checkgeneratingapplicationv6/signature-thick.png"); //replace with your image file path
            img = ImageIO.read(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        g.drawImage(img, SignatureX, SignatureY, img.getWidth(), img.getHeight(), null);

        table.put(new Pair<>(DateX, DateY), LocalDate.now().toString());
        printMemos(table, MemoX, MemoY, 10);


        int blockY = 10;
        table.put(new Pair<>(BlockX, BlockY), "Period Begin: " + check.PeriodBegin);
        table.put(new Pair<>(BlockX + 250, BlockY), "Period End: " + check.PeriodEnd);
        table.put(new Pair<>(BlockX, BlockY + blockY), "Today's Date: " + LocalDate.now());
        table.put(new Pair<>(BlockX, BlockY + 2 * blockY), "Employee Name: " + employee.getName());
        table.put(new Pair<>(BlockX + 250, BlockY + 2 * blockY), "Social Security: " + getLastFourDigitsOfSSN());

        /*Hashtable<String, Pair<BigDecimal, BigDecimal>> blockTable = new Hashtable<>();
        blockTable.put("Hour", new Pair<>(check.Hours(), ytdHours));
        blockTable.put("Rate", new Pair<>(check.HourlyRate(), null));
        blockTable.put("Revenue", new Pair<>(check.income(), ytdRevenue));
        blockTable.put("Overtime", new Pair<>(check.overtimeIncome(), ytdOvertimeHours));
        blockTable.put("O. Rate", new Pair<>(check.OvertimeHourlyRate(), null));
        blockTable.put("O. Revenue", new Pair<>(check.overtimeIncome(), ytdOvertimeRevenue));
        blockTable.put("Total Revenue", new Pair<>(check.totalRevenue(), ytdTotalRevenue));
        blockTable.put("Deduction", new Pair<>(check.deduction(), ytdDeduction));
        blockTable.put("Income", new Pair<>(check.income(), ytdIncome));*/

        insertBlock(BlockY + (int) (blockY * 4.5), table, new String[]{"Hour", "Rate", "Revenue", "Overtime", "Rate", "Revenue", "Total Revenue", "Deduction", "Income"});
        insertBlock(BlockY + (blockY * 6), table, new String[]{String.valueOf(
                check.Hours),
                check.HourlyRate.toString(),
                moneyToString(check.revenue()),
                check.OverTimeHours.toString(),
                check.OvertimeHourlyRate.toString(),
                moneyToString(check.overtimeRevenue()),
                moneyToString(check.totalRevenue()),
                moneyToString(check.deduction()),
                moneyToString(check.income())});
        insertBlock(BlockY + (int) (blockY * 7.5), table, new String[]{"YTD", "", "", "", "", "", "", "", ""});
        insertBlock(BlockY + (blockY * 9), table, new String[]{
                ytdHours.toString(),
                "",
                moneyToString(ytdRevenue),
                ytdOvertimeHours.toString(),
                "",
                moneyToString(ytdOvertimeRevenue),
                moneyToString(ytdTotalRevenue),
                moneyToString(ytdDeduction),
                moneyToString(ytdIncome)});

        insertBlock(BlockY + (blockY * 10), table, new String[]{"Social Security", "", "", moneyToString(check.socialSecurity()), "", "", "", moneyToString(ytdSSN), ""});
        insertBlock(BlockY + (blockY * 11), table, new String[]{"Federal Medicare", "", "", moneyToString(check.federalMedicare()), "", "", "", moneyToString(ytdMedicare), ""});
        insertBlock(BlockY + (blockY * 12), table, new String[]{"Federal Tax", "", "", moneyToString(check.federalTax()), "", "", "", moneyToString(ytdTax), ""});

        table.forEach((k, v) -> g.drawString(v, k.getKey(), k.getValue()));

        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }

    private void printMemos(Hashtable<Pair<Integer, Integer>, String> table, int x, int y, int yChange) {
        String[] lines = CheckController2.Check.getMemoArray(check, 4, 45);
        int y1 = y;
        for (String line : lines) {
            table.put(new Pair<>(x, y1), line);
            y1 += yChange;
        }
    }

    String getLastFourDigitsOfSSN() {
        Pattern pattern = Pattern.compile("\\d{4}(?!\\d)");
        Matcher matcher = pattern.matcher(employee.getSocialSecurityNumber());
        String lastFourDigits = "";
        while (matcher.find()) {
            lastFourDigits = matcher.group();
        }
        return lastFourDigits;
    }

    void print() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        boolean ok = job.printDialog();
        if (ok) {
            try {
                job.print();
            } catch (PrinterException ex) {
                /* The job did not successfully complete */
            }
        }
    }

    public void insertBlock(int Y, Hashtable<Pair<Integer, Integer>, String> input, String[] array) {
        if (array.length != 9) {
            throw new RuntimeException("Array length must be 9");
        }
        int[] offsets = {0, 45, 80, 130, 180, 220, 267, 345, 405};

        for (int i = 0; i < array.length; i++) {
            input.put(new Pair<>(offsets[i], Y), array[i]);
        }
    }
}
