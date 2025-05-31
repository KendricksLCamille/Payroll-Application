using Avalonia.Controls;
using Avalonia.Markup.Xaml;

namespace Check_Generating_Application_Version_6.Views;

public partial class EmployeeView : UserControl
{
    public EmployeeView()
    {
        InitializeComponent();
    }

    private void InitializeComponent()
    {
        AvaloniaXamlLoader.Load(this);
    }
}