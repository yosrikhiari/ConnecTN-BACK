    package tn.esprit.spring.connectn.Services.CROWDFUNDING;


    import com.stripe.exception.StripeException;
    import com.stripe.model.Customer;
    import com.stripe.model.PaymentIntent;
    import com.stripe.model.PaymentMethod;
    import com.stripe.model.Refund;
    import com.stripe.param.CustomerCreateParams;
    import com.stripe.param.PaymentIntentCreateParams;
    import com.stripe.param.PaymentMethodAttachParams;
    import com.stripe.param.RefundCreateParams;
    import org.springframework.stereotype.Service;
    import com.stripe.Stripe;
    import org.springframework.beans.factory.annotation.Value;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import tn.esprit.spring.connectn.DTO.CROWDFUNDING.DonationDTO;

    import javax.annotation.PostConstruct;
    @Service
    public class StripeService {
        private static final Logger logger = LoggerFactory.getLogger(StripeService.class);

        @Value("${stripe.api.key}")
        private String stripeApiKey;

        @PostConstruct
        public void init() {
            Stripe.apiKey = stripeApiKey; // Initialize Stripe with the API key
            logger.info("Stripe API key initialized: {}", stripeApiKey);
        }


        public PaymentIntent createPaymentWithPaymentMethod(DonationDTO donationDTO) throws StripeException {
            try {
                // 1. Create or retrieve customer
                Customer customer = Customer.create(
                        CustomerCreateParams.builder()
                                .setEmail(donationDTO.getEmail())
                                .build()
                );

                // 2. Attach payment method to customer
                PaymentMethod paymentMethod = PaymentMethod.retrieve(donationDTO.getPaymentMethodId());
                paymentMethod.attach(
                        PaymentMethodAttachParams.builder()
                                .setCustomer(customer.getId())
                                .build()
                );

                // 3. Create payment intent with customer
                PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                        .setAmount((long)(donationDTO.getAmount() * 100))
                        .setCurrency(donationDTO.getCurrency().toLowerCase())
                        .setDescription("Donation to campaign: " + donationDTO.getCampaignId())
                        .setPaymentMethod(donationDTO.getPaymentMethodId())
                        .setCustomer(customer.getId())  // Important: Attach customer
                        .setConfirm(true)
                        .setOffSession(true)
                        .setReceiptEmail(donationDTO.getEmail())
                        .build();

                return PaymentIntent.create(params);
            } catch (StripeException e) {
                logger.error("Stripe payment failed: {}", e.getMessage());
                throw e;
            }
        }

        public void createRefund(String paymentIntentId, long amount) throws StripeException {
            logger.info("Attempting refund for PaymentIntent: {}, Amount: {}",
                    paymentIntentId, amount);

            // Validate parameters
            if (paymentIntentId == null || paymentIntentId.isEmpty()) {
                throw new IllegalArgumentException("PaymentIntent ID cannot be null or empty");
            }
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }

            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId)
                    .setAmount(amount)
                    .build();  // Removed .setCurrency()

            try {
                Refund refund = Refund.create(params);
                logger.info("Refund successful: {}", refund.getId());
            } catch (StripeException e) {
                logger.error("Refund failed: {}", e.getMessage());
                throw e;
            }
        }


    }