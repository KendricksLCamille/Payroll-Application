using System;

namespace Check_Generating_Application_Version_6.Models;

[Serializable]
public class Settings
{
    private static readonly float Font = 10;

    /*
     * The magic numbers come from the hand calculated pixel to inches made in the powershell version of this application
     * They are here as defualt values so to have them if the client wishes to restore back to default positons of values
     */

    public float NameH { get; set; } = 100;
    public float NameV { get; set; } = 100;
    public float NameFont { get; set; } = Font;

    public float TotalIncomeH { get; set; } = 675;
    public float TotalIncomeV { get; set; } = 118;
    public float TotalIncomeFont { get; set; } = Font;

    public float TotalIncomeTextH { get; set; } = 50;
    public float TotalIncomeTextV { get; set; } = 150;
    public float TotalIncomeTextFont { get; set; } = Font;

    public float DateH { get; set; } = 500;
    public float DateV { get; set; } = 175;
    public float DateFont { get; set; } = Font;

    public float Name2H { get; set; } = 100;
    public float Name2V { get; set; } = 200;
    public float Name2Font { get; set; } = Font;

    public float StreetNameH { get; set; } = 100;
    public float StreetNameV { get; set; } = 225;
    public float StreetNameFont { get; set; } = Font;

    public float AreaH { get; set; } = 100;
    public float AreaV { get; set; } = 250;
    public float AreaFont { get; set; } = Font;

    public float SignatureH { get; set; } = 500;
    public float SignatureV { get; set; } = 197.5F;

    public float PeriodBeginH { get; set; } = 15;
    public float PeriodBeginV { get; set; } = 400;
    public float PeriodBeginFont { get; set; } = Font;

    public float PeriodEndH { get; set; } = 300;
    public float PeriodEndV { get; set; } = 400;
    public float PeriodEndFont { get; set; } = Font;

    public float Date2H { get; set; } = 15;
    public float Date2V { get; set; } = 430;
    public float Date2Font { get; set; } = Font;

    public float Name3H { get; set; } = 15;
    public float Name3V { get; set; } = 460;
    public float Name3Font { get; set; } = Font;

    public float SocialSecurityH { get; set; } = 300;
    public float SocialSecurityV { get; set; } = 460;
    public float SocialSecurityFont { get; set; } = Font;

    public float MonthlyHeaderHeight { get; set; } = 490;
    public float NumberHeight { get; set; } = 515;
    public float YearToDateHeaderHeight { get; set; } = 540;
    public float YearToDateNumbersHeight { get; set; } = 565;
    public float DeductionsHeight { get; set; } = 590;
}