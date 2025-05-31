using System;

namespace Check_Generating_Application_Version_6.Models;

public class TaxYear : AbstractModel
{
    private DateTimeOffset _begin = DateTimeOffset.Now, _end = DateTimeOffset.Now.AddYears(1);

    public DateTimeOffset Begin
    {
        get => _begin;
        set
        {
            if (End <= value) End = value.AddYears(1);
            _begin = value;
        }
    }

    public DateTimeOffset End
    {
        get => _end;
        set
        {
            if (value <= Begin) Begin = value.AddYears(-1);
            _end = value;
        }
    }

    public bool InRange(DateTimeOffset dStart)
    {
        var start = dStart >= Begin;
        var end = dStart < End;
        return start && end;
    }

    public override string ToString()
    {
        return $"{Begin.DateTime.ToLongDateString()} - {End.DateTime.ToLongDateString()}";
    }

    public override bool Equals(object? obj)
    {
        if (obj is null) return false;

        var dateTime = (TaxYear)obj;
        return BasicDatetimeOffsetEquals(dateTime.Begin, Begin) && BasicDatetimeOffsetEquals(dateTime.End, End);
    }

    public static bool BasicDatetimeOffsetEquals(DateTimeOffset a, DateTimeOffset b)
    {
        return a.Day == b.Day && a.Month == b.Month && a.Year == b.Year;
    }

    public override int GetHashCode()
    {
        return (int)Begin.DateTime.Ticks;
    }
}