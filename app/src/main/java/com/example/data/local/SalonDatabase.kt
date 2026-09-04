package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Appointment
import com.example.data.model.ClientLoyalty
import com.example.data.model.CustomerFeedback
import com.example.data.model.FinancialTransaction
import com.example.data.model.LoyaltyReward
import com.example.data.model.Product
import com.example.data.model.Professional
import com.example.data.model.SalonService
import com.example.data.model.StockMovement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        Appointment::class,
        FinancialTransaction::class,
        Professional::class,
        SalonService::class,
        ClientLoyalty::class,
        LoyaltyReward::class,
        Product::class,
        StockMovement::class,
        CustomerFeedback::class
    ],
    version = 2,
    exportSchema = false
)
abstract class SalonDatabase : RoomDatabase() {
    abstract fun salonDao(): SalonDao

    companion object {
        @Volatile
        private var INSTANCE: SalonDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SalonDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SalonDatabase::class.java,
                    "salon_gestao_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(SalonDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SalonDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.salonDao())
                }
            }
        }

        suspend fun populateInitialData(dao: SalonDao) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()
            val todayStr = dateFormat.format(calendar.time)

            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val tomorrowStr = dateFormat.format(calendar.time)

            calendar.add(Calendar.DAY_OF_YEAR, -2)
            val yesterdayStr = dateFormat.format(calendar.time)

            // Current Month prefix for transactions e.g. "2026-09"
            val monthPrefix = todayStr.substring(0, 7)

            // 1. Professionals
            val professionals = listOf(
                Professional(
                    id = 1,
                    name = "Camila Duarte",
                    role = "Hair Stylist & Colorista",
                    phone = "11987654321",
                    commissionPercent = 50,
                    workingDays = "Terça a Sábado",
                    workingHours = "09:00 - 19:00",
                    active = true,
                    colorHex = 0xFF9E475A
                ),
                Professional(
                    id = 2,
                    name = "Beatriz Lima",
                    role = "Nail Designer & Manicure",
                    phone = "11976543210",
                    commissionPercent = 60,
                    workingDays = "Segunda a Sábado",
                    workingHours = "08:30 - 18:30",
                    active = true,
                    colorHex = 0xFF7A4E82
                ),
                Professional(
                    id = 3,
                    name = "Lucas Santana",
                    role = "Barbeiro & Visagista",
                    phone = "11965432109",
                    commissionPercent = 50,
                    workingDays = "Terça a Domingo",
                    workingHours = "10:00 - 20:00",
                    active = true,
                    colorHex = 0xFF3D6B58
                ),
                Professional(
                    id = 4,
                    name = "Mariana Rios",
                    role = "Estética Facial & Sobrancelhas",
                    phone = "11954321098",
                    commissionPercent = 55,
                    workingDays = "Quarta a Sábado",
                    workingHours = "09:00 - 18:00",
                    active = true,
                    colorHex = 0xFFB57A2A
                )
            )
            dao.insertAllProfessionals(professionals)

            // 2. Services
            val services = listOf(
                SalonService(
                    id = 1,
                    name = "Corte Feminino & Escova Modelada",
                    category = "Cabelo",
                    price = 140.0,
                    durationMinutes = 60,
                    loyaltyPointsEarned = 14,
                    description = "Diagnóstico capilar personalizado, lavagem relaxante com massagem craniana, corte com visagismo e finalização com escova modelada.",
                    iconType = "haircut"
                ),
                SalonService(
                    id = 2,
                    name = "Mechas & Iluminação Glow",
                    category = "Coloração",
                    price = 380.0,
                    durationMinutes = 180,
                    loyaltyPointsEarned = 38,
                    description = "Técnica exclusiva de mechas com pó descolorante protetor, matização perolada ou dourada e tratamento pós-química intensivo.",
                    iconType = "coloring"
                ),
                SalonService(
                    id = 3,
                    name = "Hidratação Profunda Caviar Wella",
                    category = "Tratamentos",
                    price = 180.0,
                    durationMinutes = 50,
                    loyaltyPointsEarned = 18,
                    description = "Tratamento de alta performance com extrato de caviar e ácido hialurônico para regeneração imediata de fios ressecados.",
                    iconType = "treatment"
                ),
                SalonService(
                    id = 4,
                    name = "Manicure & Pedicure Spa dos Pés",
                    category = "Unhas",
                    price = 85.0,
                    durationMinutes = 75,
                    loyaltyPointsEarned = 9,
                    description = "Cutilagem combinada e esmaltação de alta durabilidade, esfoliação com sais aromáticos, hidratação profunda e massagem.",
                    iconType = "manicure"
                ),
                SalonService(
                    id = 5,
                    name = "Alongamento em Fibra de Vidro",
                    category = "Unhas",
                    price = 210.0,
                    durationMinutes = 120,
                    loyaltyPointsEarned = 21,
                    description = "Estruturação natural e resistente com filamentos de fibra importada, acabamento fino e esmaltação em gel inclusa.",
                    iconType = "manicure"
                ),
                SalonService(
                    id = 6,
                    name = "Barboterapia & Corte Fade Masculino",
                    category = "Barba",
                    price = 110.0,
                    durationMinutes = 50,
                    loyaltyPointsEarned = 11,
                    description = "Corte moderno com degradê perfeito, toalha quente com óleos essenciais, navalhado preciso e balm calmante pós-barba.",
                    iconType = "barber"
                ),
                SalonService(
                    id = 7,
                    name = "Design de Sobrancelhas com Henna",
                    category = "Estética",
                    price = 75.0,
                    durationMinutes = 40,
                    loyaltyPointsEarned = 8,
                    description = "Mapeamento facial geométrico, remoção cuidadosa com pinça e aplicação de henna orgânica personalizada ao tom dos fios.",
                    iconType = "spa"
                ),
                SalonService(
                    id = 8,
                    name = "Limpeza de Pele Profunda & Spa Facial",
                    category = "Estética",
                    price = 190.0,
                    durationMinutes = 75,
                    loyaltyPointsEarned = 19,
                    description = "Higienização profunda com vapor de ozônio, extração indolor de cravos, máscara calmante de argila e fototerapia LED.",
                    iconType = "spa"
                ),
                SalonService(
                    id = 9,
                    name = "Coloração Global & Banho de Brilho",
                    category = "Coloração",
                    price = 220.0,
                    durationMinutes = 90,
                    loyaltyPointsEarned = 22,
                    description = "Cobertura perfeita de brancos ou mudança de tom com coloração premium enriquecida com óleos nutritivos.",
                    iconType = "coloring"
                )
            )
            dao.insertAllServices(services)

            // 3. Appointments
            val appointments = listOf(
                Appointment(
                    id = 1,
                    clientName = "Fernanda Silva",
                    clientPhone = "11991234567",
                    serviceId = 1,
                    serviceName = "Corte & Escova Modelada",
                    servicePrice = 140.0,
                    professionalId = 1,
                    professionalName = "Camila Duarte",
                    appointmentDate = todayStr,
                    appointmentTime = "10:00",
                    status = "CONFIRMADO",
                    paymentStatus = "PAGO_PIX",
                    notes = "Prefere escova com pontas para fora"
                ),
                Appointment(
                    id = 2,
                    clientName = "Juliana Costa",
                    clientPhone = "11992345678",
                    serviceId = 4,
                    serviceName = "Manicure & Pedicure Spa",
                    servicePrice = 85.0,
                    professionalId = 2,
                    professionalName = "Beatriz Lima",
                    appointmentDate = todayStr,
                    appointmentTime = "11:30",
                    status = "AGENDADO",
                    paymentStatus = "PENDENTE",
                    notes = "Esmalte vermelho clássico"
                ),
                Appointment(
                    id = 3,
                    clientName = "Rodrigo Mendes",
                    clientPhone = "11993456789",
                    serviceId = 6,
                    serviceName = "Barboterapia & Corte Fade",
                    servicePrice = 110.0,
                    professionalId = 3,
                    professionalName = "Lucas Santana",
                    appointmentDate = todayStr,
                    appointmentTime = "14:00",
                    status = "AGENDADO",
                    paymentStatus = "PENDENTE",
                    notes = "Toalha quente extra"
                ),
                Appointment(
                    id = 4,
                    clientName = "Patrícia Alves",
                    clientPhone = "11994567890",
                    serviceId = 2,
                    serviceName = "Mechas & Iluminação Glow",
                    servicePrice = 380.0,
                    professionalId = 1,
                    professionalName = "Camila Duarte",
                    appointmentDate = todayStr,
                    appointmentTime = "15:30",
                    status = "CONFIRMADO",
                    paymentStatus = "PAGO_CARTAO",
                    notes = "Retoque de raiz + tonalização perolada"
                ),
                Appointment(
                    id = 5,
                    clientName = "Carolina Rocha",
                    clientPhone = "11995678901",
                    serviceId = 5,
                    serviceName = "Alongamento em Fibra de Vidro",
                    servicePrice = 210.0,
                    professionalId = 2,
                    professionalName = "Beatriz Lima",
                    appointmentDate = tomorrowStr,
                    appointmentTime = "09:30",
                    status = "AGENDADO",
                    paymentStatus = "PENDENTE",
                    notes = "Formato amendoado"
                ),
                Appointment(
                    id = 6,
                    clientName = "Renata Martins",
                    clientPhone = "11996789012",
                    serviceId = 3,
                    serviceName = "Hidratação Profunda Caviar",
                    servicePrice = 180.0,
                    professionalId = 1,
                    professionalName = "Camila Duarte",
                    appointmentDate = yesterdayStr,
                    appointmentTime = "16:00",
                    status = "CONCLUIDO",
                    paymentStatus = "PAGO_PIX",
                    notes = "Cabelo muito ressecado"
                )
            )
            dao.insertAllAppointments(appointments)

            // 4. Financial Transactions
            val transactions = listOf(
                FinancialTransaction(
                    id = 1,
                    description = "Corte & Escova - Fernanda Silva",
                    amount = 140.0,
                    type = "RECEITA",
                    category = "SERVICO",
                    date = todayStr,
                    paymentMethod = "PIX"
                ),
                FinancialTransaction(
                    id = 2,
                    description = "Mechas Glow - Patrícia Alves",
                    amount = 380.0,
                    type = "RECEITA",
                    category = "SERVICO",
                    date = todayStr,
                    paymentMethod = "CARTAO_CREDITO"
                ),
                FinancialTransaction(
                    id = 3,
                    description = "Hidratação Caviar - Renata Martins",
                    amount = 180.0,
                    type = "RECEITA",
                    category = "SERVICO",
                    date = yesterdayStr,
                    paymentMethod = "PIX"
                ),
                FinancialTransaction(
                    id = 4,
                    description = "Compra Estoque Shampoos e Máscaras Wella",
                    amount = 450.0,
                    type = "DESPESA",
                    category = "PRODUTOS",
                    date = todayStr,
                    paymentMethod = "PIX"
                ),
                FinancialTransaction(
                    id = 5,
                    description = "Conta de Energia Elétrica (Salão)",
                    amount = 280.0,
                    type = "DESPESA",
                    category = "CONTAS",
                    date = yesterdayStr,
                    paymentMethod = "PIX"
                ),
                FinancialTransaction(
                    id = 6,
                    description = "Aluguel Mensal Espaço Salão",
                    amount = 1800.0,
                    type = "DESPESA",
                    category = "ALUGUEL",
                    date = "$monthPrefix-01",
                    paymentMethod = "PIX"
                ),
                FinancialTransaction(
                    id = 7,
                    description = "Venda Kit Manutenção Home Care",
                    amount = 220.0,
                    type = "RECEITA",
                    category = "PRODUTOS",
                    date = yesterdayStr,
                    paymentMethod = "CARTAO_CREDITO"
                ),
                FinancialTransaction(
                    id = 8,
                    description = "Repasse Comissão Camila Duarte (Semana 1)",
                    amount = 620.0,
                    type = "DESPESA",
                    category = "COMISSAO",
                    date = "$monthPrefix-02",
                    paymentMethod = "PIX"
                )
            )
            dao.insertAllTransactions(transactions)

            // 5. Client Loyalty
            val loyaltyClients = listOf(
                ClientLoyalty(
                    id = 1,
                    clientName = "Fernanda Silva",
                    clientPhone = "11991234567",
                    totalPoints = 145,
                    tier = "OURO",
                    totalVisits = 8,
                    lastVisitDate = todayStr,
                    totalSpent = 1420.0
                ),
                ClientLoyalty(
                    id = 2,
                    clientName = "Patrícia Alves",
                    clientPhone = "11994567890",
                    totalPoints = 230,
                    tier = "VIP_DIAMANTE",
                    totalVisits = 12,
                    lastVisitDate = todayStr,
                    totalSpent = 2450.0
                ),
                ClientLoyalty(
                    id = 3,
                    clientName = "Juliana Costa",
                    clientPhone = "11992345678",
                    totalPoints = 65,
                    tier = "PRATA",
                    totalVisits = 4,
                    lastVisitDate = yesterdayStr,
                    totalSpent = 580.0
                ),
                ClientLoyalty(
                    id = 4,
                    clientName = "Mariana Castro",
                    clientPhone = "11998877665",
                    totalPoints = 95,
                    tier = "PRATA",
                    totalVisits = 5,
                    lastVisitDate = "$monthPrefix-01",
                    totalSpent = 920.0
                ),
                ClientLoyalty(
                    id = 5,
                    clientName = "Renata Martins",
                    clientPhone = "11996789012",
                    totalPoints = 18,
                    tier = "BRONZE",
                    totalVisits = 1,
                    lastVisitDate = yesterdayStr,
                    totalSpent = 180.0
                )
            )
            dao.insertAllLoyaltyClients(loyaltyClients)

            // 6. Loyalty Rewards
            val rewards = listOf(
                LoyaltyReward(
                    id = 1,
                    title = "Desconto de R$ 25 no Próximo Serviço",
                    pointsRequired = 50,
                    description = "Válido para qualquer procedimento no salão",
                    discountValue = 25.0,
                    iconName = "discount"
                ),
                LoyaltyReward(
                    id = 2,
                    title = "Escova Modelada ou Barboterapia Cortesia",
                    pointsRequired = 100,
                    description = "Ganhe uma finalização perfeita sem custo",
                    discountValue = 70.0,
                    iconName = "brush"
                ),
                LoyaltyReward(
                    id = 3,
                    title = "Hidratação Profunda Caviar Wella",
                    pointsRequired = 150,
                    description = "Tratamento reconstrutor intensivo completo",
                    discountValue = 180.0,
                    iconName = "sparkles"
                ),
                LoyaltyReward(
                    id = 4,
                    title = "Spa dos Pés & Manicure Gel Grátis",
                    pointsRequired = 120,
                    description = "Cuidado relaxante para mãos e pés",
                    discountValue = 110.0,
                    iconName = "spa"
                )
            )
            dao.insertAllRewards(rewards)

            // 7. Inventory Products
            val products = listOf(
                Product(
                    id = 1,
                    name = "Shampoo Fusion Intense Repair 1000ml",
                    category = "Capilar",
                    sku = "WEL-FUS-1000",
                    currentStock = 2,
                    minStockAlert = 4, // Low stock!
                    costPrice = 120.0,
                    salePrice = 195.0,
                    unit = "un",
                    supplier = "Wella Professionals Brasil"
                ),
                Product(
                    id = 2,
                    name = "Máscara Reconstrutora Oil Reflections 500ml",
                    category = "Capilar",
                    sku = "WEL-OIL-500",
                    currentStock = 6,
                    minStockAlert = 3,
                    costPrice = 98.0,
                    salePrice = 165.0,
                    unit = "un",
                    supplier = "Wella Professionals Brasil"
                ),
                Product(
                    id = 3,
                    name = "Pó Descolorante Blondme 450g",
                    category = "Coloração",
                    sku = "SCH-BLM-450",
                    currentStock = 1,
                    minStockAlert = 3, // Low stock!
                    costPrice = 145.0,
                    salePrice = 0.0,
                    unit = "un",
                    supplier = "Schwarzkopf Professional"
                ),
                Product(
                    id = 4,
                    name = "Oxidante Igora Royal 20 Vol 1000ml",
                    category = "Coloração",
                    sku = "SCH-OX20-1000",
                    currentStock = 7,
                    minStockAlert = 4,
                    costPrice = 45.0,
                    salePrice = 0.0,
                    unit = "un",
                    supplier = "Schwarzkopf Professional"
                ),
                Product(
                    id = 5,
                    name = "Esmalte Risqué Vermelho Desejo 8ml",
                    category = "Esmaltes",
                    sku = "RSQ-VM-08",
                    currentStock = 10,
                    minStockAlert = 4,
                    costPrice = 4.20,
                    salePrice = 8.50,
                    unit = "un",
                    supplier = "Coty Brasil"
                ),
                Product(
                    id = 6,
                    name = "Top Coat Efeito Gel Vefic 120ml",
                    category = "Esmaltes",
                    sku = "VEF-TC-120",
                    currentStock = 2,
                    minStockAlert = 3, // Low stock!
                    costPrice = 32.0,
                    salePrice = 0.0,
                    unit = "un",
                    supplier = "Vefic Cosméticos"
                ),
                Product(
                    id = 7,
                    name = "Kit Fibra de Vidro Filamentos Più Bella",
                    category = "Esmaltes",
                    sku = "PIU-FIB-100",
                    currentStock = 5,
                    minStockAlert = 2,
                    costPrice = 65.0,
                    salePrice = 0.0,
                    unit = "kit",
                    supplier = "Più Bella Nail Care"
                ),
                Product(
                    id = 8,
                    name = "Óleo Para Barba & Bigode Amadeirado 30ml",
                    category = "Barba",
                    sku = "BAR-OIL-30",
                    currentStock = 8,
                    minStockAlert = 3,
                    costPrice = 28.0,
                    salePrice = 58.0,
                    unit = "un",
                    supplier = "Barba de Respeito"
                ),
                Product(
                    id = 9,
                    name = "Sérum Facial Vitamina C 15% 30ml",
                    category = "Pele & Estética",
                    sku = "EST-SER-30",
                    currentStock = 4,
                    minStockAlert = 2,
                    costPrice = 52.0,
                    salePrice = 110.0,
                    unit = "un",
                    supplier = "Adcos Dermocosméticos"
                ),
                Product(
                    id = 10,
                    name = "Caixa Luvas Nitrílicas Pretas 100un (Tam M)",
                    category = "Descartáveis",
                    sku = "DSC-LUV-100",
                    currentStock = 1,
                    minStockAlert = 5, // Low stock!
                    costPrice = 38.0,
                    salePrice = 0.0,
                    unit = "cx",
                    supplier = "Medix Brasil"
                )
            )
            dao.insertAllProducts(products)

            // 8. Stock Movements
            val movements = listOf(
                StockMovement(
                    id = 1,
                    productId = 1,
                    productName = "Shampoo Fusion Intense Repair 1000ml",
                    type = "ENTRADA",
                    quantity = 6,
                    reason = "Compra Fornecedor",
                    notes = "Nota Fiscal 48291 - Wella",
                    date = "$todayStr 08:30"
                ),
                StockMovement(
                    id = 2,
                    productId = 1,
                    productName = "Shampoo Fusion Intense Repair 1000ml",
                    type = "SAIDA",
                    quantity = 4,
                    reason = "Uso em Procedimento",
                    notes = "Lavatório e bancadas da equipe",
                    date = "$todayStr 11:00"
                ),
                StockMovement(
                    id = 3,
                    productId = 5,
                    productName = "Esmalte Risqué Vermelho Desejo 8ml",
                    type = "ENTRADA",
                    quantity = 12,
                    reason = "Compra Fornecedor",
                    notes = "Reposição semanal",
                    date = "$yesterdayStr 09:15"
                ),
                StockMovement(
                    id = 4,
                    productId = 5,
                    productName = "Esmalte Risqué Vermelho Desejo 8ml",
                    type = "SAIDA",
                    quantity = 2,
                    reason = "Venda Balcão",
                    notes = "Venda para cliente Juliana Costa",
                    date = "$yesterdayStr 14:20"
                ),
                StockMovement(
                    id = 5,
                    productId = 10,
                    productName = "Caixa Luvas Nitrílicas Pretas 100un (Tam M)",
                    type = "SAIDA",
                    quantity = 2,
                    reason = "Uso em Procedimento",
                    notes = "Uso em procedimentos químicos",
                    date = "$todayStr 09:00"
                )
            )
            dao.insertAllStockMovements(movements)

            // 9. Customer Feedback & Ratings
            val feedbacks = listOf(
                CustomerFeedback(
                    id = 1,
                    appointmentId = 1,
                    clientName = "Fernanda Silva",
                    clientPhone = "11991234567",
                    professionalId = 1,
                    professionalName = "Camila Duarte",
                    serviceId = 1,
                    serviceName = "Corte Feminino & Escova Modelada",
                    rating = 5,
                    comment = "A Camila é simplesmente espetacular! Entendeu exatamente o corte que eu queria, o cabelo ficou super leve e a escova durou o dia todo. Recomendo de olhos fechados!",
                    date = todayStr
                ),
                CustomerFeedback(
                    id = 2,
                    appointmentId = 4,
                    clientName = "Patrícia Alves",
                    clientPhone = "11994567890",
                    professionalId = 1,
                    professionalName = "Camila Duarte",
                    serviceId = 2,
                    serviceName = "Mechas & Iluminação Glow",
                    rating = 5,
                    comment = "Minhas mechas ficaram perfeitas, zero manchas e o loiro super saudável com o tratamento. Ambiente agradável e café delicioso!",
                    date = yesterdayStr
                ),
                CustomerFeedback(
                    id = 3,
                    appointmentId = 2,
                    clientName = "Juliana Costa",
                    clientPhone = "11992345678",
                    professionalId = 2,
                    professionalName = "Beatriz Lima",
                    serviceId = 4,
                    serviceName = "Manicure & Pedicure Spa dos Pés",
                    rating = 5,
                    comment = "A Bia tem uma delicadeza ímpar na cutilagem! O spa dos pés é super relaxante e a esmaltação dura quase 15 dias sem lascar.",
                    date = todayStr
                ),
                CustomerFeedback(
                    id = 4,
                    appointmentId = 5,
                    clientName = "Carolina Rocha",
                    clientPhone = "11995678901",
                    professionalId = 2,
                    professionalName = "Beatriz Lima",
                    serviceId = 5,
                    serviceName = "Alongamento em Fibra de Vidro",
                    rating = 4,
                    comment = "Alongamento impecável, super natural e resistente. Só demorou 15 min a mais do previsto, mas o resultado final valeu super a pena.",
                    date = yesterdayStr
                ),
                CustomerFeedback(
                    id = 5,
                    appointmentId = 3,
                    clientName = "Rodrigo Mendes",
                    clientPhone = "11993456789",
                    professionalId = 3,
                    professionalName = "Lucas Santana",
                    serviceId = 6,
                    serviceName = "Barboterapia & Corte Fade Masculino",
                    rating = 5,
                    comment = "Corte degradê na régua e a barboterapia com toalha quente é o melhor momento da semana. Lucas é fera demais!",
                    date = todayStr
                ),
                CustomerFeedback(
                    id = 6,
                    appointmentId = 7,
                    clientName = "Mariana Castro",
                    clientPhone = "11998877665",
                    professionalId = 4,
                    professionalName = "Mariana Rios",
                    serviceId = 7,
                    serviceName = "Design de Sobrancelhas com Henna",
                    rating = 5,
                    comment = "O desenho ficou super simétrico e harmônico com meu rosto. A cor da henna ficou perfeita, nem muito escura nem artificial.",
                    date = yesterdayStr
                )
            )
            dao.insertAllFeedbacks(feedbacks)
        }
    }
}
