using Avalonia;
using Avalonia.Controls;
using Avalonia.Markup.Xaml;

namespace Check_Generating_Application_Version_6.Views;

public partial class CheckView : UserControl
{
    public CheckView()
    {
        InitializeComponent();
    }

    private void InitializeComponent()
    {
        AvaloniaXamlLoader.Load(this);
    }
}