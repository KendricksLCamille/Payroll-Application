using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Reactive;
using Check_Generating_Application_Version_6.Models;
using Microsoft.EntityFrameworkCore;
using ReactiveUI;

namespace Check_Generating_Application_Version_6.ViewModels;

//Created so I don't need to pass the type to have access to the buttons
public abstract class AbstractViewModel : ViewModelBase
{
    public ReactiveCommand<Unit, Unit> AddButton { get; protected set; } = null!;
    public ReactiveCommand<Unit, Unit> DeleteButton { get; protected set; } = null!;
    public ReactiveCommand<Unit, Unit> SaveButton { get; protected set; } = null!;
    public ReactiveCommand<Unit, AbstractViewModel> SpecialButton { get; protected set; } = null!;
    public ReactiveCommand<Unit, AbstractViewModel> SpecialButton2 { get; protected set; } = null!;
    protected abstract string SpecialText { get; }
    protected virtual string SpecialText2 => "Disabled";

    protected abstract AbstractViewModel SpecialSelected();
    protected abstract AbstractViewModel SpecialSelected2();
}

public abstract class AbstractViewModelPlus<T> : AbstractViewModel where T : AbstractModel
{
    //If it equals, the _selected will reference hte previous object rather than be a new reference. I could fix with duplicating hte object but I'll just be lazy.
    protected readonly CheckGeneratingContext2022
        InternalDb =
            new(); //I only keep a link to the database available when I needed so I don't have to deal with reference issues.

    protected ObservableCollection<T> _items = null!;
    
    private int _selectedIndex = -1;
    
    protected virtual IEnumerable<T> FilteredDb => Db;

    protected void Init()
    {
        // ReSharper disable once VirtualMemberCallInConstructor
        Items = new ObservableCollection<T>(FilteredDb);
        SelectedIndex = Db.Count() - 1;
        
        // Whenever the selected index changes, set the values of the inputs
        this.WhenAnyValue(x => x.SelectedIndex, x => x >= 0).Subscribe(x =>
        {
            //If nothing there, don't do anything
            if (!FilteredDb.Any()) { return; }
            
            // If the selected index is -1, just try to use the last item
            var selected = x == false ? FilteredDb.ToList().LastOrDefault() : FilteredDb.ToList()[SelectedIndex];

            // If it is still null, then just give up
            if (selected is null) return;
            
            // Set the inner class
            // The rationality for doing this is to make not filling out this function a compile time error rather than a runtime error.
            SetInternals(selected);
        });
        
        // ReSharper disable once VirtualMemberCallInConstructor
        AddButton = ReactiveCommand.Create(AddSelected, AddEnabled);
        // ReSharper disable once VirtualMemberCallInConstructor
        DeleteButton = ReactiveCommand.Create(DeleteSelected, DeleteEnabled);
        // ReSharper disable once VirtualMemberCallInConstructor
        SaveButton = ReactiveCommand.Create(SaveSelected, SaveEnabled);
        // ReSharper disable once VirtualMemberCallInConstructor
        SpecialButton = ReactiveCommand.Create(SpecialSelected, SpecialEnabled);
        // ReSharper disable once VirtualMemberCallInConstructor
        SpecialButton2 = ReactiveCommand.Create(SpecialSelected2, SpecialEnabled2);
    }

    protected abstract IObservable<bool> AddEnabled { get; }
    protected virtual IObservable<bool> DeleteEnabled => this.WhenAnyValue(x => x.SelectedIndex, x => x >= 0);
    protected virtual IObservable<bool> SaveEnabled => DeleteEnabled;
    protected virtual IObservable<bool> SpecialEnabled => DeleteEnabled;
    protected virtual IObservable<bool> SpecialEnabled2 => this.WhenAny(x => x.SelectedIndex, x => false);

    protected abstract T Add();
    
    /*public T? Selected
    {
        get => _selected;
        set => this.RaiseAndSetIfChanged(ref _selected, value);
    }*/

    public virtual ObservableCollection<T> Items
    {
        get => _items;
        set => this.RaiseAndSetIfChanged(ref _items, value);
    }

    public int SelectedIndex
    {
        get => _selectedIndex;
        set => this.RaiseAndSetIfChanged(ref _selectedIndex, value);
    }

    protected abstract DbSet<T> Db { get; }

    protected abstract void SetInternals(T t);
    
    private void AddSelected()
    {
        _ = Db.Add(Add());
        _ = InternalDb.SaveChanges();
        Items = new ObservableCollection<T>(FilteredDb);
    }

    private void DeleteSelected()
    {
        Db.Remove(FilteredDb.ToList()[SelectedIndex]);
        InternalDb.SaveChanges();
        Items = new ObservableCollection<T>(FilteredDb);
        SelectedIndex = Db.Count() - 1;
    }

    private void SaveSelected()
    {
        var og = Db.ToList()[SelectedIndex];
        var copy = Add();
        copy.ID = og.ID;

        Db.Remove(og);
        Db.Update(copy);
        InternalDb.SaveChanges();
        var tempSelectedIndex = SelectedIndex;
        Items = new ObservableCollection<T>(FilteredDb);
        SelectedIndex = tempSelectedIndex;
    }
    
    /// <summary>
    /// Returns the current object from database for a valid index else return null
    /// </summary>
    public T? Current => SelectedIndex < 0 ? null : FilteredDb.ToList()[SelectedIndex];
}