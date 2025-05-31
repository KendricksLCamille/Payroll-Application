using System;
using System.Collections.Generic;
using System.Linq;
using Check_Generating_Application_Version_6.Models;
using Microsoft.EntityFrameworkCore;
using ReactiveUI;

namespace Check_Generating_Application_Version_6.ViewModels;

public class TaxYearViewModel : AbstractViewModelPlus<TaxYear>
{
    //TODO: Default all dates to the 01/01/CurrentYear to 01/01/(Current+1)
    //TODO: Make the period begin and end work properly so when period end is to low, make period begin lower
    private DateTimeOffset _begin = DateTimeOffset.Now, 
        _end = DateTimeOffset.Now;

    public DateTimeOffset Begin
    {
        get => _begin;
        set => this.RaiseAndSetIfChanged(ref _begin, value);
    }

    public DateTimeOffset End
    {
        get => _end;
        set => this.RaiseAndSetIfChanged(ref _end, value);
    }

    protected override TaxYear Add()
    {
        return new TaxYear()
        {
            Begin = Begin,
            End = End
        };
    }

    protected override DbSet<TaxYear> Db => InternalDb.TaxYears;
    protected override void SetInternals(TaxYear selected)
    {
        Begin = selected.Begin;
        End = selected.End;
    }

    protected override string SpecialText => "Go To Employee Page";

    protected override IObservable<bool> AddEnabled
    {
        get
        {
            bool RetTrue(DateTimeOffset begin, DateTimeOffset end, int _)
            {
                var current = Add();

                return !Enumerable.Contains(Db, current);
            }

            var temp = this.WhenAnyValue(x => x.Begin, x => x.End, x => x.SelectedIndex, RetTrue);

            

            return temp;
        }
    }
    
    protected override AbstractViewModel SpecialSelected()
    {
        return new EmployeeViewModel(this);
    }

    protected override AbstractViewModel SpecialSelected2()
    {
        throw new InvalidOperationException("This button should be disabled on this page.");
    }

    public TaxYearViewModel() => Init();
}