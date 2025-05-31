using NUnit.Framework;
using CheckApplication;

namespace CheckApplicationTest
{
    [TestFixture]
    public class DeductionTest
    {
        [SetUp]
        public void Setup()
        {
            Deductions.taxes.Add((10M, 0.10M));
            Deductions.taxes.Add((20M, 0.20M));
            Deductions.taxes.Add((40M, 0.40M));
            Deductions.taxes.Add((80M, 0.80M));
            Deductions.taxes.Add((100M, 1M));
        }

        [Test]
        [TestCase(4,0)]
        [TestCase(10,1)]
        [TestCase(20,3)]
        public void Test1(decimal income, decimal exp)
        {
            Deductions.PreTaxIncome = income;
            var actual = Deductions.FederalIncomeTaxes;
            Assert.AreEqual(exp,actual);
        }
    }

    [TestFixture]
    public class W4TaxDeductions
    {
        [Test]
        public void IsWitholdTaxCorrect()
        {

        }
    }
}