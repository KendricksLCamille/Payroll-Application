using System;
using System.Linq;
using Check_Generating_Application_Version_6.Models;
using Microsoft.EntityFrameworkCore;
using ReactiveUI;

namespace Check_Generating_Application_Version_6.ViewModels;

public class EmployeeViewModel : AbstractViewModelPlus<Employee>
{
    private string _city = string.Empty;
    private int _daysBetweenChecks = 7;
    private string _errorOutput;

    private string _name = "Default";
    private int _ssn;
    private string _state = "FL";
    private string _streetName = string.Empty;
    internal readonly TaxYearViewModel _taxYearViewModel;
    private int _zipCode;

    public EmployeeViewModel(TaxYearViewModel taxYearViewModel)
    {
        _taxYearViewModel = taxYearViewModel;
        _errorOutput = string.Empty;
        Init();
    }


    public string ErrorOutput
    {
        get => _errorOutput;
        set => this.RaiseAndSetIfChanged(ref _errorOutput, value);
    }

    public string Name
    {
        get => _name;
        set => this.RaiseAndSetIfChanged(ref _name, value);
    }

    public string StreetName
    {
        get => _streetName;
        set => this.RaiseAndSetIfChanged(ref _streetName, value);
    }

    public string City
    {
        get => _city;
        set => this.RaiseAndSetIfChanged(ref _city, value);
    }

    public string State
    {
        get => _state;
        set => this.RaiseAndSetIfChanged(ref _state, value);
    }

    public int ZipCode
    {
        get => _zipCode;
        set => this.RaiseAndSetIfChanged(ref _zipCode, value);
    }

    public int DaysBetweenChecks
    {
        get => _daysBetweenChecks;
        set => this.RaiseAndSetIfChanged(ref _daysBetweenChecks, value);
    }

    public int SSN
    {
        get => _ssn;
        set => this.RaiseAndSetIfChanged(ref _ssn, value);
    }

    protected override string SpecialText => "Go to Checks";
    protected override string SpecialText2 => "Choose Tax Year";

    protected override IObservable<bool> AddEnabled
    {
        get
        {
            return this.WhenAnyValue(
                x => x.Name,
                x => x.StreetName,
                x => x.City,
                x => x.State,
                x => x.ZipCode,
                x => x.DaysBetweenChecks,
                x => x.SSN,
                (name, street, city, state, zip, daysBetweenChecks, ssn) =>
                {
                    ErrorOutput = string.Empty; //Clear errors
                    
                    var array = new[] { name, street, city, state };
                    if (array.Any(string.IsNullOrWhiteSpace))
                    {
                        ErrorOutput = "Name, Street Address, City or State should not be empty(blank) or just spaces";
                        return false;
                    }

                    long NumberOfDigits(long input)
                    {
                        return (int)Math.Log10(input) + 1;
                    }

                    if (NumberOfDigits(zip) != 5)
                    {
                        ErrorOutput = "Zip Code needs to be made up of 5 numbers only";
                        return false;
                    }
                    
                    if (NumberOfDigits(ssn) != 9)
                    {
                        ErrorOutput = "Social Security needs 9 numbers";
                        return false;
                    }
                    return true;
                });
        }
    }

    protected override IObservable<bool> SpecialEnabled2 => this.WhenAny(x => x.SelectedIndex, x => true);
    protected override Employee Add()
    {
        var current = new Employee
        {
            Name = Name,
            City = City,
            StreetName = StreetName,
            ZipCode = ZipCode,
            DaysBetweenChecks = DaysBetweenChecks,
            State = State,
            SSN = SSN
        };

        return current;
    }

    protected override DbSet<Employee> Db => InternalDb.Employees;
    protected override void SetInternals(Employee x)
    {
        SSN = x.SSN;
        Name = x.Name;
        State = x.State;
        City = x.City;
        StreetName = x.StreetName;
        ZipCode = x.ZipCode;
        DaysBetweenChecks = x.DaysBetweenChecks;
    }

    protected override AbstractViewModel SpecialSelected()
    {
        return this;
    }

    protected override AbstractViewModel SpecialSelected2()
    {
        return _taxYearViewModel;
    }

}