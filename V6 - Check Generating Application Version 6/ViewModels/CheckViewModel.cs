using System;
using System.Collections.Generic;
using System.Linq;
using Check_Generating_Application_Version_6.Models;
using Microsoft.EntityFrameworkCore;
using ReactiveUI;

namespace Check_Generating_Application_Version_6.ViewModels;

public class CheckViewModel: AbstractViewModelPlus<Check>
{
    private string errorMessage = String.Empty;
    public Employee CurrentEmployee => _employeeViewModel.Current ?? throw new InvalidOperationException($"Check accessed the {_employeeViewModel.SelectedIndex} of the Employee database.");
    private string CurrentEmployeeStr => $"Current Employee: {CurrentEmployee.Name}";
    private TaxYear CurrentTaxYear => _employeeViewModel._taxYearViewModel.Current ?? throw new InvalidOperationException($"Check accessed the {_employeeViewModel._taxYearViewModel.SelectedIndex} of the Tax Year database.");
    private string TaxYearStr => $"Tax Year Range: {CurrentTaxYear}";

    #region Properties

    public string Notes
    {
        get => _notes;
        set => this.RaiseAndSetIfChanged(ref _notes, value);
    }
    
    public string ErrorMessage
    {
        get => errorMessage;
        set => this.RaiseAndSetIfChanged(ref errorMessage, value);
    }
    
    public DateTimeOffset PeriodBegin
    {
        get => _periodBegin;
        set => this.RaiseAndSetIfChanged(ref _periodBegin, value);
    }

    public DateTimeOffset PeriodEnd
    {
        get => _periodEnd;
        set => this.RaiseAndSetIfChanged(ref _periodEnd, value);
    }

    public decimal TotalIncome
    {
        get => _totalIncome;
        set => this.RaiseAndSetIfChanged(ref _totalIncome, value);
    }

    public decimal Revenue
    {
        get => _revenue;
        set => this.RaiseAndSetIfChanged(ref _revenue, value);
    }

    public decimal OvertimeRevenue
    {
        get => _overtimeRevenue;
        set => this.RaiseAndSetIfChanged(ref _overtimeRevenue, value);
    }

    public decimal Hours
    {
        get => _hours;
        set => this.RaiseAndSetIfChanged(ref _hours, value);
    }

    public decimal OvertimeHours
    {
        get => _overtimeHours;
        set => this.RaiseAndSetIfChanged(ref _overtimeHours, value);
    }

    public decimal Pay
    {
        get => _pay;
        set => this.RaiseAndSetIfChanged(ref _pay, value);
    }

    public decimal Rate
    {
        get => _rate;
        set => this.RaiseAndSetIfChanged(ref _rate, value);
    }

    public decimal OvertimeRate
    {
        get => _overtimeRate;
        set => this.RaiseAndSetIfChanged(ref _overtimeRate, value);
    }

    public decimal SSNRate
    {
        get => _ssnRate;
        set => this.RaiseAndSetIfChanged(ref _ssnRate, value);
    }

    public decimal FederalMedicareRate
    {
        get => _federalMedicareRate;
        set => this.RaiseAndSetIfChanged(ref _federalMedicareRate, value);
    }

    public decimal FederalTaxRate
    {
        get => _federalTaxRate;
        set => this.RaiseAndSetIfChanged(ref _federalTaxRate, value);
    }

    public decimal SSNValue
    {
        get => _ssnValue;
        set => this.RaiseAndSetIfChanged(ref _ssnValue, value);
    }

    public decimal MedicareValue
    {
        get => _medicareValue;
        set => this.RaiseAndSetIfChanged(ref _medicareValue, value);
    }

    public decimal FederalTaxValue
    {
        get => _federalTaxValue;
        set => this.RaiseAndSetIfChanged(ref _federalTaxValue, value);
    }

    public decimal Deduction
    {
        get => _deduction;
        set => this.RaiseAndSetIfChanged(ref _deduction, value);
    }

    #endregion

    #region Private Variables

    private Employee? _employee;
    private EmployeeViewModel _employeeViewModel;
    private DateTimeOffset _periodBegin = DateTimeOffset.Now;
    private DateTimeOffset _periodEnd = DateTimeOffset.Now;
    private decimal _totalIncome;
    private decimal _revenue;
    private decimal _overtimeRevenue;
    private decimal _hours;
    private decimal _overtimeHours;
    private decimal _pay;
    private decimal _rate;
    private decimal _overtimeRate;
    private decimal _ssnRate;
    private decimal _federalMedicareRate;
    private decimal _federalTaxRate;
    private decimal _ssnValue;
    private decimal _medicareValue;
    private decimal _federalTaxValue;
    private decimal _deduction;
    private readonly string _specialText = "Employees Page";
    private string _notes = string.Empty;

    #endregion

    public CheckViewModel(EmployeeViewModel e)
    {
        _employeeViewModel = e;
        Init();
    }

    protected override string SpecialText => _specialText;

    protected override AbstractViewModel SpecialSelected() => _employeeViewModel;

    protected override AbstractViewModel SpecialSelected2()
    {
        throw new InvalidOperationException("This button shouldn't be enabled");
    }

    bool CheckIsInRange(Check check)
    {
        var currentTaxYear = InternalDb.TaxYears.ToList()[this._employeeViewModel._taxYearViewModel.SelectedIndex];
        return currentTaxYear.InRange(check.PeriodBegin);
    }

    //TODO: Set Year to Date by pulling from Linq.
    protected override IEnumerable<Check> FilteredDb => Db.AsEnumerable().Where(x => x.EmployeeID == CurrentEmployee.ID)
        .Where(CheckIsInRange).OrderBy(x => x.PeriodBegin).ThenBy(x => x.PeriodEnd);

    protected override IObservable<bool> AddEnabled
    {
        get
        {
            var first = this.WhenAnyValue(
                x => x.SelectedIndex,
                x => x.PeriodBegin,
                x => x.PeriodEnd,
                x => x.Hours,
                x => x.OvertimeHours,
                x => x.Rate,
                x => x.OvertimeRate,
                x => x.SSNRate,
                x => x.FederalMedicareRate,
                x => x.FederalTaxRate,
                (x, pb, pe, h, oh, r, or, ssn, fmr, ftr) =>
                {
                    
                    //#define GiveUpIfFaile(condition, message) { printf("%s", message); return false; }
                    ErrorMessage = string.Empty;
                    
                    if (pb >= pe)
                    {
                        ErrorMessage = "Period Begin is greater than Period End";
                        return false;
                    }
                    
                    SetInternals(Add());
                    return true;
                });



            return first;
        }
    }

    private bool setInternalsWasCalled = false;
    
    protected override Check Add()
    {
        var check = new Check()
        {
            PeriodBegin = PeriodBegin.DateTime,
            PeriodEnd = PeriodEnd.DateTime,
            Hours = Hours,
            OvertimeHours = OvertimeHours,
            Rate = Rate,
            OvertimeRate = OvertimeRate,
            SSNRate = SSNRate / 100,
            FederalMedicareRate = FederalMedicareRate / 100,
            FederalTaxRate = FederalTaxRate / 100,
            EmployeeID = CurrentEmployee.ID,
            Notes = Notes
        };

        return check;
    }

    protected override DbSet<Check> Db => InternalDb.Checks;
    
    protected override void SetInternals(Check t)
    {
        if (setInternalsWasCalled) return;
        setInternalsWasCalled = true;
        
        PeriodBegin = t.PeriodBegin;
        PeriodEnd = t.PeriodEnd;
        Hours = t.Hours;
        OvertimeHours = t.OvertimeHours;
        Rate = t.Rate;
        OvertimeRate = t.OvertimeRate;
        SSNRate = t.SSNRate * 100;
        FederalMedicareRate = t.FederalMedicareRate * 100;
        FederalTaxRate = t.FederalTaxRate * 100;
        TotalIncome = t.TotalIncome;
        Revenue = t.Revenue;
        OvertimeRevenue = t.OvertimeRevenue;
        Pay = t.Pay;
        SSNValue = t.ToSSN;
        MedicareValue = t.ToMedicare;
        FederalTaxValue = t.ToFederalTax;
        Deduction = t.Deduction;
        Notes = t.Notes;
        setInternalsWasCalled = false;
    }
}