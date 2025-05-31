using System;
using System.ComponentModel.DataAnnotations;

namespace Check_Generating_Application_Version_6.Models;

[Serializable]
public abstract class AbstractModel : ICloneable
{
    [Key] public int ID { get; set; }

    public object Clone()
    {
        return MemberwiseClone();
    }
}