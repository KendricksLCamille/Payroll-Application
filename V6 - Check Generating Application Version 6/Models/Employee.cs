namespace Check_Generating_Application_Version_6.Models;

public class Employee : AbstractModel
{
    public string Name { get; set; } = "Default";
    public string StreetName { get; set; } = string.Empty;
    public string City { get; set; } = string.Empty;
    public string State { get; set; } = "FL";
    public int ZipCode { get; set; }

    public string Address => $"{StreetName} | {City}, {State} {ZipCode}";

    public int DaysBetweenChecks { get; set; } = 7;

    public int SSN { get; set; }

    public override string ToString()
    {
        return $"{Name} | {Address}";
    }
}