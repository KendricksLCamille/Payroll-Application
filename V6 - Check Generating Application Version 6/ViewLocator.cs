using System;
using Avalonia.Controls;
using Avalonia.Controls.Templates;
using Check_Generating_Application_Version_6.ViewModels;

namespace Check_Generating_Application_Version_6;

public class ViewLocator : IDataTemplate
{
    public IControl Build(object data)
    {
        var name = data.GetType().FullName!.Replace("ViewModel", "View");
        var type = Type.GetType(name);

        if (type != null) return (Control)Activator.CreateInstance(type)!;

        return new TextBlock { Text = "Not Found: " + name };
    }

    public bool Match(object data)
    {
        return data is ViewModelBase;
    }
}