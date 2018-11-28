import java.util.ArrayList;
import java.util.List;

/*
算法思想：
    遗传算法参照达尔文的进化论，认为物种都是向好的方向去发展（适者生存），因此可以认为到足够的代数之后，得到的最值可实际的最值很接近。
算法步骤：
1）随机产生一个种群；
2）计算种群的适应度、最好适应度、最差适应度、平均适应度等指标；
3）验证种群代数是否达到自己设置的阈值，如果达到结束计算，否则继续下一步计算；
4）采用转盘赌法选择可以产生下一代的父代，产生下一代种群（种群中个体数量不变）；
5）种群发生基因突变；
6）重复2、3、4、5步。

该类是一个抽象类，因此我们需要针对特定的事例编写实现类。
 */

public abstract class GeneticAlgorithm {
    // 对于遗传算法，我们需要有对应的种群以及我们需要设置的一些常量：种群数量、基因长度、基因突变个数、基因突变率等
    private List<Chromosome> population = new ArrayList<Chromosome>();
    private int popSize = 100;//种群数量
    private int geneSize;//基因最大长度
    private int maxIterNum = 500;//最大迭代次数
    private double mutationRate = 0.01;//基因变异的概率
    private int maxMutationNum = 3;//最大变异步长

    private int generation = 1;//当前遗传到第几代

    private double bestScore;//最好得分
    private double worstScore;//最坏得分
    private double totalScore;//总得分
    private double averageScore;//平均得分

    private double x; //记录历史种群中最好的X值
    private double y; //记录历史种群中最好的Y值
    private int geneI;//x y所在代数

    public GeneticAlgorithm(int geneSize) {
        this.geneSize = geneSize;
    }

    // 将上述步骤一代一代的重复执行。
    public void caculte() {
        //初始化种群
        generation = 1;
        init();
        while (generation < maxIterNum) {
            //种群遗传
            evolve();
            print();
            generation++;
        }
    }

    /**
     * @Author:lulei
     * @Description: 输出结果
     */
    private void print() {
        System.out.println("--------------------------------");
        System.out.println("the generation is:" + generation);
        System.out.println("the best y is:" + bestScore);
        System.out.println("the worst fitness is:" + worstScore);
        System.out.println("the average fitness is:" + averageScore);
        System.out.println("the total fitness is:" + totalScore);
        System.out.println("geneI:" + geneI + "\tx:" + x + "\ty:" + y);
    }


    /**
     * @Author:lulei
     * @Description: 初始化种群，在遗传算法开始时，我们需要初始化一个原始种群，这就是原始的第一代。
     */
    private void init() {
        for (int i = 0; i < popSize; i++) {
            population = new ArrayList<Chromosome>();
            Chromosome chro = new Chromosome(geneSize);
            population.add(chro);
        }
        caculteScore();
    }

    /**
     * @Author:lulei
     * @Description:种群进行遗传,选择可以产生下一代的个体之后，就要交配产生下一代。
     */
    private void evolve() {
        List<Chromosome> childPopulation = new ArrayList<Chromosome>();
        //生成下一代种群
        while (childPopulation.size() < popSize) {
            Chromosome p1 = getParentChromosome();
            Chromosome p2 = getParentChromosome();
            List<Chromosome> children = Chromosome.genetic(p1, p2);
            if (children != null) {
                for (Chromosome chro : children) {
                    childPopulation.add(chro);
                }
            }
        }
        //新种群替换旧种群
        List<Chromosome> t = population;
        population = childPopulation;
        t.clear();
        t = null;
        //基因突变
        mutation();
        //计算新种群的适应度
        caculteScore();
    }

    /**
     * @Description: 在计算完种群适应度之后，我们需要使用转盘赌法选取可以产生下一代的个体，
     *                 这里有个条件就是只有个人的适应度不小于平均适应度才会长生下一代（适者生存）。
     */
    private Chromosome getParentChromosome (){
        double slice = Math.random() * totalScore;
        double sum = 0;
        for (Chromosome chro : population) {
            sum += chro.getScore();
            if (sum > slice && chro.getScore() >= averageScore) {
                return chro;
            }
        }
        return null;
    }

    /**
     * @Author:lulei
     * @Description: 在初始种群存在后，我们需要计算种群的适应度以及最好适应度、最坏适应度和平均适应度等。
     */
    private void caculteScore() {
        setChromosomeScore(population.get(0));
        bestScore = population.get(0).getScore();
        worstScore = population.get(0).getScore();
        totalScore = 0;
        for (Chromosome chro : population) {
            setChromosomeScore(chro);
            if (chro.getScore() > bestScore) { //设置最好基因值
                bestScore = chro.getScore();
                if (y < bestScore) {
                    x = changeX(chro);
                    y = bestScore;
                    geneI = generation;
                }
            }
            if (chro.getScore() < worstScore) { //设置最坏基因值
                worstScore = chro.getScore();
            }
            totalScore += chro.getScore();
        }
        averageScore = totalScore / popSize;
        //因为精度问题导致的平均值大于最好值，将平均值设置成最好值
        averageScore = averageScore > bestScore ? bestScore : averageScore;
    }

    /**
     * 基因突变,在产生下一代的过程中，可能会发生基因变异。
     */
    private void mutation() {
        for (Chromosome chro : population) {
            if (Math.random() < mutationRate) { //发生基因突变
                int mutationNum = (int) (Math.random() * maxMutationNum);
                chro.mutation(mutationNum);
            }
        }
    }

    /**
     * @param chro
     * @Author:lulei
     * @Description: 在计算个体适应度的时候，我们需要根据基因计算对应的Y值，这里我们设置两个抽象方法，具体实现由类的实现去实现。
     */
    private void setChromosomeScore(Chromosome chro) {
        if (chro == null) {
            return;
        }
        double x = changeX(chro);
        double y = caculateY(x);
        chro.setScore(y);

    }

    /**
     * @param chro
     * @return
     * @Author:lulei
     * @Description: 将二进制转化为对应的X
     */
    public abstract double changeX(Chromosome chro);


    /**
     * @param x
     * @return
     * @Author:lulei
     * @Description: 根据X计算Y值 Y=F(X)
     */
    public abstract double caculateY(double x);

    public void setPopulation(List<Chromosome> population) {
        this.population = population;
    }

    public void setPopSize(int popSize) {
        this.popSize = popSize;
    }

    public void setGeneSize(int geneSize) {
        this.geneSize = geneSize;
    }

    public void setMaxIterNum(int maxIterNum) {
        this.maxIterNum = maxIterNum;
    }

    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
    }

    public void setMaxMutationNum(int maxMutationNum) {
        this.maxMutationNum = maxMutationNum;
    }

    public double getBestScore() {
        return bestScore;
    }

    public double getWorstScore() {
        return worstScore;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
