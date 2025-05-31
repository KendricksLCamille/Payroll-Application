using System;
using Check_Generating_Application_Version_6.Models;
using ReactiveUI;

namespace Check_Generating_Application_Version_6.ViewModels;

public class MainWindowViewModel : ViewModelBase
{
    private AbstractViewModel content = new TaxYearViewModel();

    public MainWindowViewModel()
    {
        TaxYearViewModel GenerateTaxYearView()
        {
            var taxYearViewModel = new TaxYearViewModel();
            taxYearViewModel.SpecialButton.Subscribe(x =>
            {
                Content = GenerateEmployeeView(taxYearViewModel);
            });
            return taxYearViewModel;
        }
        
        CheckViewModel GenerateCheckViewModel(EmployeeViewModel employeeViewModel)
        {
            var vm = new CheckViewModel(employeeViewModel);
            vm.SpecialButton.Subscribe(x =>
            {
                Content = x;
            });
            return vm;
        }

        EmployeeViewModel GenerateEmployeeView(TaxYearViewModel taxYearViewModel)
        {
            var employeeViewModel = new EmployeeViewModel(taxYearViewModel);
            
            // Check and Taxes can only go in 1 direction so they don't set up the SpecialButton2
            // Tax Year > < Employee > < Check
            employeeViewModel.SpecialButton.Subscribe(x =>
            {
                Content = GenerateCheckViewModel(employeeViewModel);
                Content.SpecialButton.Subscribe(x =>
                {
                    Content = x;
                });
                
            });

            // Tax Year is stored within the object so there is no need to call a functions
            employeeViewModel.SpecialButton2.Subscribe(x =>
            {
                Content = x;
            });
            return employeeViewModel;
        }

        var taxYearViewModel = GenerateTaxYearView();
        /*Content = taxYearViewModel;
        return;*/
        if (taxYearViewModel.SelectedIndex >= 0)
            Content = GenerateEmployeeView(taxYearViewModel);
        else
            Content = taxYearViewModel;
    }

    public string Greeting => "Welcome to Avalonia!";

    public AbstractViewModel Content
    {
        get => content;
        private set => this.RaiseAndSetIfChanged(ref content, value);
    }
}