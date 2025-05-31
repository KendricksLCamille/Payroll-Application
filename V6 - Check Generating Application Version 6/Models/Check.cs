using System;
using System.Linq;

namespace Check_Generating_Application_Version_6.Models;

public class Check : AbstractModel
{
    public string Notes { get; set; } = string.Empty;
    public DateTime PeriodBegin { get; set; } = DateTime.Now;
    public DateTime PeriodEnd { get; set; } = DateTime.Now;

    public decimal TotalIncome => decimal.Round(Pay - Deduction, 2, MidpointRounding.ToPositiveInfinity);
    public int EmployeeID { get; set; }

    public override string ToString()
    {
        return PeriodBegin.ToShortDateString();
    }

    public void ClearData()
    {
        PeriodBegin = default;
        PeriodEnd = default;
        Hours = default;
        OvertimeHours = default;
        Rate = default;
        OvertimeRate = default;
        SSNRate = default;
        FederalMedicareRate = default;
        FederalTaxRate = default;
    }

    public bool Equals(Check? other)
    {
        if (other == null) return false;

        //I only need to chekc the decimal values. ID, EmployeeID and Employee don't change till save
        var properties = typeof(Check).GetProperties().Where(x => x.PropertyType == typeof(decimal) || x.PropertyType == typeof(DateTime));
        foreach (var prop in properties)
        {
            var thisProp = prop.GetValue(this);
            var otherProp = prop.GetValue(other);

            if (otherProp is null || thisProp is null) return false;

            var isEqual = otherProp.Equals(thisProp);
            if (!isEqual)
                //If even one isn't equal, they are not equal
                return false;
        }

        return true;
    }

    #region Revenue

    public decimal Hours { get; set; }
    public decimal OvertimeHours { get; set; }
    public decimal Rate { get; set; }
    public decimal OvertimeRate { get; set; }

    public decimal Revenue => decimal.Round(Hours * Rate, 2, MidpointRounding.ToPositiveInfinity);

    public decimal OvertimeRevenue =>
        decimal.Round(OvertimeHours * OvertimeRate, 2, MidpointRounding.ToPositiveInfinity);

    public decimal Pay => decimal.Round(Revenue + OvertimeRevenue, 2, MidpointRounding.ToPositiveInfinity);

    #endregion

    #region Deduction

    public decimal SSNRate { get; set; }
    public decimal FederalMedicareRate { get; set; }
    public decimal FederalTaxRate { get; set; }

    public decimal ToSSN => decimal.Round(Pay * SSNRate, 2, MidpointRounding.ToPositiveInfinity);
    public decimal ToMedicare => decimal.Round(Pay * FederalMedicareRate, 2, MidpointRounding.ToPositiveInfinity);
    public decimal ToFederalTax => decimal.Round(Pay * FederalTaxRate, 2, MidpointRounding.ToPositiveInfinity);

    public decimal Deduction =>
        decimal.Round(ToSSN + ToMedicare + ToFederalTax, 2, MidpointRounding.ToPositiveInfinity);

    #endregion
}