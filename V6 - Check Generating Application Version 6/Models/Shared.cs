using Microsoft.EntityFrameworkCore;
using SQLitePCL;

namespace Check_Generating_Application_Version_6.Models;

public sealed class CheckGeneratingContext2022 : DbContext
{
    public CheckGeneratingContext2022()
    {
        Batteries_V2.Init();
        Database.EnsureCreated();
    }

    public DbSet<Employee> Employees { get; set; } = null!;
    public DbSet<Check> Checks { get; set; } = null!;
    public DbSet<TaxYear> TaxYears { get; set; } = null!;

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        var dbPath = "./Data2.db3";

        optionsBuilder.UseSqlite($"Filename={dbPath}");
    }
}

public sealed class CheckGeneratingContext2023 : DbContext
{
    public CheckGeneratingContext2023()
    {
        Batteries_V2.Init();
        Database.EnsureCreated();
    }

    public DbSet<Employee> Employees { get; set; } = null!;
    public DbSet<Check> Checks { get; set; } = null!;
    public DbSet<TaxYear> TaxYears { get; set; } = null!;

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        var dbPath = "./Data2023.db3";

        optionsBuilder.UseSqlite($"Filename={dbPath}");
    }
}